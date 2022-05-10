package io.github.smdawe.kafka.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
public class Api {

  private static final Logger LOG = LoggerFactory.getLogger(Api.class);

  static final String MESSAGE_ENDPOINT = "/message";

  @Value("${kafka.topic}")
  private String topic;

  @Autowired
  KafkaTemplate<String, String> kafkaTemplate;

  @PostMapping(value = MESSAGE_ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> message(@RequestBody MessageBody messageBody) {
    try {
      kafkaTemplate.send(topic, UUID.randomUUID().toString(), messageBody.getValue()).completable().join();

      return new ResponseEntity<>(HttpStatus.CREATED);
    } catch (Throwable t) {
      LOG.error(t.getMessage(), t);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }


  }
}
