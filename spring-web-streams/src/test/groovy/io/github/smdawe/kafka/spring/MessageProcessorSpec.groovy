package io.github.smdawe.kafka.spring

import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KStream
import spock.lang.Specification

class MessageProcessorSpec extends Specification {

  MessageProcessor messageProcessor
  StreamConsumer streamConsumer
  String topic = UUID.randomUUID().toString()

  void setup() {
    streamConsumer = Mock(StreamConsumer)
    messageProcessor = new MessageProcessor(streamConsumer: streamConsumer, topic: topic)
  }

  void 'ensure pipeline behaviour'() {
    given:
      StreamsBuilder streamsBuilder = Mock(StreamsBuilder)
      KStream stream = Mock(KStream)
      1 * streamsBuilder.stream(topic, _) >> stream

    when:
      messageProcessor.pipeline(streamsBuilder)
    then:
      1 * stream.foreach(_)
  }
}
