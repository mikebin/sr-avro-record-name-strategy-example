package clients;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

import io.confluent.avro.User;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;

public class AvroConsumer {

  private static final String TOPIC1 = "shared-schema-topic1";
  private static final String TOPIC2 = "shared-schema-topic2";

  public static void main(String[] args) throws Exception {
    System.out.println("*** Starting Avro Consumer ***");
    Properties settings = new Properties();
    settings.put(ConsumerConfig.CLIENT_ID_CONFIG, "avro-consumer-v0.1.0");
    settings.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
    settings.put(ConsumerConfig.GROUP_ID_CONFIG, "avro-consumer-group");
    settings.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    settings.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
    settings.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");
    settings.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);

    final KafkaConsumer<String, User> consumer = new KafkaConsumer<>(settings);
    consumer.subscribe(List.of(TOPIC1, TOPIC2));

    while (true) {
      ConsumerRecords<String, User> records = consumer.poll(Duration.ofMillis(100));
      for (ConsumerRecord<String, User> record : records) {
        System.out.format("Read '%s' from topic %s, partition %s%n", record.value(),
                          record.topic(), record.partition());
      }
    }
  }
}