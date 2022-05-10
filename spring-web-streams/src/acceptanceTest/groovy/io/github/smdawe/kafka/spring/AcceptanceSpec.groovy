package io.github.smdawe.kafka.spring

import groovy.json.JsonOutput
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import spock.lang.Shared
import spock.util.concurrent.BlockingVariable

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

@AutoConfigureMockMvc
@SpringBootTest
class AcceptanceSpec extends KafkaSpecification {

  @Shared
  BlockingVariable<String> blockingVariable

  @Autowired
  MockMvc mockMvc

  @SpringBean
  StreamConsumer streamConsumer = specStreamConsumer()

  void 'post a message'() {
    given:
      String messageValue = UUID.randomUUID().toString()
      Map<String, String> message = [value: messageValue]

    when:
      MvcResult result = mockMvc.perform(post(Api.MESSAGE_ENDPOINT)
        .content(JsonOutput.toJson(message))
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
      ).andReturn()

    then:
      result.response.status == HttpStatus.CREATED.value()
      blockingVariable.get() == messageValue
  }

  private boolean messageReceived(String message) {
    return true
  }

  private SpecStreamConsumer specStreamConsumer() {
    blockingVariable = new BlockingVariable<>(15)
    return new SpecStreamConsumer(blockingVariable)
  }
}
