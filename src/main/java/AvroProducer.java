import avro.Customer;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.io.ByteArrayOutputStream;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.Future;

public class AvroProducer {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers","localhost:9092");
        props.put("acks", "1");
        props.put("retries", 0);
        props.put("compression.type", "snappy");        
        props.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer","org.apache.kafka.common.serialization.ByteArraySerializer");

        Producer<String,byte[]> producer = new KafkaProducer<>(props);
        ProducerRecord<String,byte[]> record;
        String topic = "java-avro";
        Random random = new Random();
        int sleep = 2000;

        try {
            while (true) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                DatumWriter<Customer> datum = new SpecificDatumWriter<>(Customer.class);
                BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(stream, null);

                Customer customer = Customer.newBuilder()
                        .setId("id_" + random.nextInt(200))
                        .setName("name_" + random.nextInt(200))
                        .build();
                datum.write(customer, encoder);
                encoder.flush();
                record = new ProducerRecord<>(topic, stream.toByteArray());

                System.out.println("Sending " + customer);
                Future<RecordMetadata> resultFuture = producer.send(record);
                System.out.println("Message sent to partition " + resultFuture.get().partition());
                System.out.println("Offset of message is " + resultFuture.get().offset());
                System.out.println();
                stream.close();
                Thread.sleep(sleep);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            producer.close();
        }
    }
}
