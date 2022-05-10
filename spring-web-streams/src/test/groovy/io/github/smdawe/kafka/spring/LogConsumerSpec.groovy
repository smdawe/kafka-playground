package io.github.smdawe.kafka.spring

import org.slf4j.Logger
import spock.lang.Specification

import java.lang.reflect.Field
import java.lang.reflect.Modifier

class LogConsumerSpec extends Specification {

  Logger logger
  void setup() {
    logger = Mock(Logger)

    // hacky
    Field field = LogConsumer.getDeclaredField('LOG')
    field.setAccessible(true)

    Field modifiersField = Field.class.getDeclaredField("modifiers")
    modifiersField.setAccessible(true)
    modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL)

    field.set(null, logger)
  }

  void 'ensure message is logged'() {
    given:
      LogConsumer logConsumer = new LogConsumer()
      String message = UUID.randomUUID()

    when:
      logConsumer.consume(UUID.randomUUID().toString(), message)

    then:
      1 * logger.info(message)
  }
}
