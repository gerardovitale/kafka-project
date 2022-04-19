package gve.kafka.wikimedia;

import io.github.cdimascio.dotenv.Dotenv;

public class ConfigReader {
    private static ConfigReader configObject;
    private final String bootStrapServers;
    private final String topic;
    private final String url;
    private final String lingerMilliSec;
    private final String batchSize;
    private final String compressionType;


    private ConfigReader() {
        Dotenv dotenv = Dotenv.configure().load();
        this.bootStrapServers = dotenv.get("KAFKA_BOOTSTRAP_SERVER");
        this.topic = dotenv.get("KAFKA_TOPIC");
        this.url = dotenv.get("STREAM_URL");
        this.lingerMilliSec = dotenv.get("KAFKA_LINGER_MS_CONFIG");
        this.batchSize = dotenv.get("KAFKA_BATCH_SIZE_CONFIG");
        this.compressionType = dotenv.get("KAFKA_COMPRESSION_TYPE_CONFIG");
    }

    public static ConfigReader getInstance() {
        if (configObject == null) {
            configObject = new ConfigReader();
        }
        return configObject;
    }

    public String getBootStrapServers() {
        return bootStrapServers;
    }

    public String getTopic() {
        return topic;
    }

    public String getUrl() {
        return url;
    }

    public String getLingerMilliSec() {
        return lingerMilliSec;
    }

    public String getBatchSize() {
        return batchSize;
    }

    public String getCompressionType() {
        return compressionType;
    }
}
