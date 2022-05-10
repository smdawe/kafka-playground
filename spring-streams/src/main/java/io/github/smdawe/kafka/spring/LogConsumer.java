package io.github.smdawe.kafka.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LogConsumer implements StreamConsumer {
  private static final Logger LOG = LoggerFactory.getLogger(MessageProcessor.class);

  @Override
  public void consume(String key, String content) {
    LOG.info(content);
  }
}
