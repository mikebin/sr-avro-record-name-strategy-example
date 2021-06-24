package clients;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

import io.confluent.avro.User;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.subject.RecordNameStrategy;

public class AvroProducer {

  private static final String TOPIC1 = "shared-schema-topic1";
  private static final String TOPIC2 = "shared-schema-topic2";

  public static void main(String[] args) throws Exception {
    System.out.println("*** Starting Avro Producer ***");
    Properties settings = new Properties();
    settings.put(ProducerConfig.CLIENT_ID_CONFIG, "avro-producer-v0.1.0");
    settings.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
    settings.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    settings.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
    settings.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
    settings.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");
    settings.put(AbstractKafkaSchemaSerDeConfig.AUTO_REGISTER_SCHEMAS, false);
    settings.put(AbstractKafkaSchemaSerDeConfig.USE_LATEST_VERSION, true);
    settings.put(AbstractKafkaSchemaSerDeConfig.VALUE_SUBJECT_NAME_STRATEGY, RecordNameStrategy.class);

    final Producer<String, User> producer = new KafkaProducer<>(settings);

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      System.out.println("### Stopping Avro Producer ###");
      producer.close();
    }));

    User user = new User("Jane", 1, "green");

    ProducerRecord<String, User> record = new ProducerRecord<>(TOPIC1, user);
    send(producer, record);

    record = new ProducerRecord<>(TOPIC2, user);
    send(producer, record);
  }

  private static void send(Producer<String, User> producer,
                    final ProducerRecord<String, User> record) {
    producer.send(record, (recordMetadata, e) -> {
      if (e != null) {
        e.printStackTrace();
      }
      else {
        System.out.format("Produced record to topic %s, partition %d%n", recordMetadata.topic(),
                          recordMetadata.partition());
      }
    });
  }
}