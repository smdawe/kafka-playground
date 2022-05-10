package io.github.smdawe.kafka.spring

import io.github.smdawe.envvar.EnvironmentVariables
import io.github.smdawe.envvar.extensions.WithEnvironmentVariables
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.containers.wait.strategy.Wait
import spock.lang.Shared
import spock.lang.Specification

@WithEnvironmentVariables
class KafkaSpecification extends Specification {

  static final int KAFKA_PORT = 29092
  static final String KAFKA_START_MESSAGE = '.*started.*'//[2022-05-09 15:37:42,684] INFO [KafkaServer id=1] started (kafka.server.KafkaServer)
  static final String KAFKA_CONTAINER = 'kafka_1'
  static final String KAFKA_BOOTSTRAP_ADDRESS_ENV = 'KAFKA_BOOTSTRAP_ADDRESS'

  @Shared
  DockerComposeContainer environment

  @Shared
  EnvironmentVariables environmentVariables = new EnvironmentVariables()

  void setupSpec() {
    environment  = new DockerComposeContainer(new File("docker/docker-compose.yml"))
            .withExposedService(KAFKA_CONTAINER, KAFKA_PORT, Wait.forLogMessage(KAFKA_START_MESSAGE, 1))

    environment.start()

    String kafkaUrl = "${environment.getServiceHost(KAFKA_CONTAINER, KAFKA_PORT)}:${environment.getServicePort(KAFKA_CONTAINER, KAFKA_PORT)}"

    environmentVariables.addEnvironmentVariable(KAFKA_BOOTSTRAP_ADDRESS_ENV, kafkaUrl)
  }

  void cleanupSpec() {
    environment.stop()
  }
}
