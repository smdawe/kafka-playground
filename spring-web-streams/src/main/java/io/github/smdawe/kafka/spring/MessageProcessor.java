package io.github.smdawe.kafka.spring;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MessageProcessor {

  private static final Serde<String> STRING_SERDE = Serdes.String();

  @Value("${kafka.topic}")
  private String topic;

  @Autowired
  private StreamConsumer streamConsumer;

  @Autowired
  public void pipeline(StreamsBuilder streamsBuilder){
    streamsBuilder.stream(topic, Consumed.with(STRING_SERDE, STRING_SERDE)).foreach(streamConsumer::consume);
  }

}
