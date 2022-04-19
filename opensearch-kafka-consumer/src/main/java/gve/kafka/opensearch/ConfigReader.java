package gve.kafka.opensearch;

import io.github.cdimascio.dotenv.Dotenv;

public class ConfigReader {
    private static ConfigReader configObject;
    private final String bootStrapServers;
    private final String topic;
    private final String groupId;
    private final String autoOffset;
    private final String enableAutoCommit;
    private final String indexName;
    private final String opensearchServer;

    private ConfigReader() {
        Dotenv dotenv = Dotenv.configure().load();
        this.bootStrapServers = dotenv.get("KAFKA_BOOTSTRAP_SERVER");
        this.topic = dotenv.get("KAFKA_TOPIC");
        this.groupId = dotenv.get("KAFKA_GROUP_ID");
        this.autoOffset = dotenv.get("KAFKA_AUTO_OFFSET_RESET");
        this.enableAutoCommit = dotenv.get("KAFKA_ENABLE_AUTO_COMMIT");
        this.opensearchServer = dotenv.get("OPENSEARCH_SERVER");
        this.indexName = dotenv.get("OPENSEARCH_INDEX");
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

    public String getGroupId() {
        return groupId;
    }

    public String getAutoOffset() {
        return autoOffset;
    }

    public String getEnableAutoCommit() {
        return enableAutoCommit;
    }

    public String getIndexName() {
        return indexName;
    }

    public String getOpensearchServer() {
        return opensearchServer;
    }
}
