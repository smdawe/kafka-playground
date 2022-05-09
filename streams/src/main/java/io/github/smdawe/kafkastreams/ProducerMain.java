package io.github.smdawe.kafkastreams;

import net.logstash.logback.marker.Markers;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class ProducerMain {

  private static final String SERVER = "localhost:29092";
  private static final String APP_ID = "kafka-producer";
  private static final String TOPIC = "test-topic";

  private static final Logger LOG = LoggerFactory.getLogger(ProducerMain.class);

  public static final void main(final String[] args) {
    final Properties properties = new Properties();

    properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, SERVER);

    properties.put(ProducerConfig.CLIENT_ID_CONFIG, APP_ID);

    properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
    properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

    KafkaProducer<Long, String> producer = new KafkaProducer<>(properties);

    try {
      sendMessages(producer);
    } catch (ExecutionException | InterruptedException e) {
      LOG.error(e.getMessage(), e);
    }

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      producer.flush();
      producer.close();
    }));
  }

  private static void sendMessages(KafkaProducer<Long, String> producer) throws InterruptedException, ExecutionException {
    Long count = 0L;
    while (count < 100) {
      final ProducerRecord<Long, String> record = new ProducerRecord<>(TOPIC, ++count, UUID.randomUUID().toString());
      final RecordMetadata metadata = producer.send(record).get();

      Map<String, Object> markers = Map.of("record.key", record.key(), "record.partition", metadata.partition());
      LOG.info(Markers.appendEntries(markers), "Sent message");

      Thread.sleep(1_000);
    }
  }
}
