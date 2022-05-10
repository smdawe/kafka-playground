package io.github.smdawe.kafka.spring;

public interface StreamConsumer {
  void consume(String key, String content);
}
