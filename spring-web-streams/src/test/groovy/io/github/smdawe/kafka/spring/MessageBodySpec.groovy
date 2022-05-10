package io.github.smdawe.kafka.spring

import spock.lang.Specification

class MessageBodySpec extends Specification {

  MessageBody messageBody

  void setup() {
    messageBody = new MessageBody()
  }

  void 'value'() {
    given:
      String value = UUID.randomUUID()

    when:
      messageBody.setValue(value)

    then:
      messageBody.value == value
  }
}
