package io.github.smdawe.kafka.streams;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ConsumerMain {

  private static final String SERVER = "localhost:29092";
  private static final String APP_ID = "kafka-producer";
  private static final String TOPIC = "test-topic";

  private static final Logger LOG = LoggerFactory.getLogger(ConsumerMain.class);

  public static final void main(final String[] args) {
    final Properties properties = new Properties();

    properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, SERVER);

    properties.put(StreamsConfig.APPLICATION_ID_CONFIG, APP_ID);
    properties.put(StreamsConfig.CLIENT_ID_CONFIG, APP_ID);

    properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.Long().getClass().getName());
    properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

    final StreamsBuilder builder = new StreamsBuilder();

    KStream<Long, String> stream = builder.stream(TOPIC);
    stream.foreach(ConsumerMain::logOutput);

    final KafkaStreams streams = new KafkaStreams(builder.build(), properties);
    streams.cleanUp();
    streams.start();

    // Add shutdown hook to respond to SIGTERM and gracefully close Kafka Streams
    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
      @Override
      public void run() {
        streams.close();
      }
    }));
  }

  private static void logOutput(Long key, String content) {
    LOG.info(content);
  }

}
