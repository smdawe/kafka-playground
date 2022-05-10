package io.github.smdawe.kafka.spring


import spock.util.concurrent.BlockingVariable

class SpecStreamConsumer implements StreamConsumer {

  BlockingVariable<String> blockingVariable

  SpecStreamConsumer(BlockingVariable<String> blockingVariable) {
    this.blockingVariable = blockingVariable
  }

  @Override
  void consume(String key, String content) {
    blockingVariable.set(content)
  }
}
