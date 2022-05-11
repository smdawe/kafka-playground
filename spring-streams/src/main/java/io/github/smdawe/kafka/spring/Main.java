package io.github.smdawe.kafka.spring;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.StreamJoined;
import org.apache.kafka.streams.kstream.ValueJoiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SpringBootApplication
public class Main implements CommandLineRunner {

  private static final Logger LOG = LoggerFactory.getLogger(Main.class);

  private static final Serde<String> STRING_SERDE = Serdes.String();

  @Value("${kafka.primaryTopic}")
  private String primaryTopic;

  @Value("${kafka.secondaryTopic}")
  private String secondaryTopic;

  @Autowired
  KafkaTemplate<String, String> kafkaTemplate;

  public static void main(String[] args) {
    SpringApplication.run(Main.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    try {
      for (int i = 0; i < 10; i++) {
        String id = UUID.randomUUID().toString();
        kafkaTemplate.send(primaryTopic, id, i + "a");
        kafkaTemplate.send(secondaryTopic, id, i + "b");
      }

    } catch (Throwable t) {
      LOG.error(t.getMessage(), t);
    }
  }

  
  @Autowired
  public void primaryPipeline(StreamsBuilder streamsBuilder){
    streamsBuilder.stream(primaryTopic, Consumed.with(STRING_SERDE, STRING_SERDE)).foreach((a, b) -> consume(a, b, "primary"));
  }

  @Autowired
  public void secondaryPipeline(StreamsBuilder streamsBuilder){
    streamsBuilder.stream(secondaryTopic, Consumed.with(STRING_SERDE, STRING_SERDE)).foreach((a, b) -> consume(a, b, "secondary"));
  }

  @Autowired
  public void joinPipeline(StreamsBuilder streamsBuilder){
    KStream<String, String> joinStream = streamsBuilder.stream(secondaryTopic, Consumed.with(STRING_SERDE, STRING_SERDE));
    ValueJoiner<String, String, String> joiner = (a, b) -> a + " - " + b;

    streamsBuilder.stream(primaryTopic, Consumed.with(STRING_SERDE, STRING_SERDE))
      .join(joinStream, joiner, JoinWindows.of(Duration.ofSeconds(5)),
        StreamJoined.with(STRING_SERDE, STRING_SERDE, STRING_SERDE))
    .foreach((a, b) -> consume(a, b, "joined"));
  }



  private void consume(String key, String value, String type) {
    LOG.info(type + " - " + key + " - " + value);
  }
}
