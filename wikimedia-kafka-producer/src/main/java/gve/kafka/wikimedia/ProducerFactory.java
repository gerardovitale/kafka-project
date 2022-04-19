package gve.kafka.wikimedia;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class ProducerFactory {

    public static KafkaProducer<String, String> createKafkaProducer(ConfigReader config) {
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootStrapServers());
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.LINGER_MS_CONFIG, config.getLingerMilliSec());
        properties.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, config.getBatchSize());
        properties.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, config.getCompressionType());

        return new KafkaProducer<>(properties);
    }
}
