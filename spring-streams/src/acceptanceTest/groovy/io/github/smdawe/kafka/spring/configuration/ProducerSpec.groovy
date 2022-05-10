package io.github.smdawe.kafka.spring.configuration

import io.github.smdawe.envvar.extensions.WithEnvironmentVariables
import io.github.smdawe.kafka.spring.KafkaSpecification
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.KafkaTemplate
import spock.lang.Timeout

@SpringBootTest(classes = Producer)
class ProducerSpec extends KafkaSpecification {

  @Autowired
  KafkaTemplate<String, String> kafkaTemplate

  @Timeout(3)
  void 'make sure a message can be sent'() {
    expect: 'Test will fail if it cant send a message'
      kafkaTemplate.send('test', 'test')
  }
}
