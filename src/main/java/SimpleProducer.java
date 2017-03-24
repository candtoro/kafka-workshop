import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;
import java.util.Random;
public class SimpleProducer {    
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers","localhost:9092");
        props.put("acks", "all");
        props.put("retries", 1);
        props.put("compression.type", "snappy");        
        props.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");

        Producer<String,String> producer = new KafkaProducer<>(props);
        ProducerRecord<String,String> record;
        String topic = "java-simple";
        Random random = new Random();
        int sleep = 2000;

        try {
            while (true) {
                String value = "Random " + random.nextInt(500);
                record = new ProducerRecord<>(topic, value);
                System.out.println("Sending " + value);
                producer.send(record);
                Thread.sleep(sleep);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            producer.close();
        }
    }
}
