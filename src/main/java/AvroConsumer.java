import avro.Customer;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.Properties;

public class AvroConsumer {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "test");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        String topic = "java-avro";
        KafkaConsumer<String, byte[]> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(topic));

        try {
            while (true) {
                ConsumerRecords<String, byte[]> records = consumer.poll(100);
                for (ConsumerRecord<String, byte[]> record : records) {
                    DatumReader<Customer> datum = new SpecificDatumReader<>(Customer.class);
                    Customer customer = datum.read(null, DecoderFactory.get().binaryDecoder(record.value(), null));
                    System.out.println("Message received from partition " + record.partition());
                    System.out.println("Message offset " + record.offset());
                    System.out.println("Message key " + record.key());
                    System.out.println("Customer " + customer);
                    System.out.println();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            consumer.close();
        }
    }
}
