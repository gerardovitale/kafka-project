package gve.kafka.opensearch;

import com.google.gson.JsonParser;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.opensearch.OpenSearchStatusException;
import org.opensearch.action.bulk.BulkRequest;
import org.opensearch.action.bulk.BulkResponse;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.client.RequestOptions;
import org.opensearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;

public class OpenSearchConsumer {

    private static final Logger logger = LoggerFactory.getLogger(OpenSearchConsumer.class.getSimpleName());

    private static String extractIdFromMessage(String message) {
        return JsonParser.parseString(message)
                .getAsJsonObject()
                .get("meta")
                .getAsJsonObject()
                .get("id")
                .getAsString();
    }

    public static void main(String[] args) {
        ConfigReader config = ConfigReader.getInstance();
        KafkaConsumer<String, String> consumer = ConsumerFactory.createKafkaConsumer(config);
        consumer.subscribe(Collections.singleton(config.getTopic()));
        OpenSearchClient openSearchClient = OpenSearchClient.createOpenSearchClient(config);

        try (openSearchClient; consumer) {
            openSearchClient.createIndex();
            logger.info("OpenSearch wikimedia index is created");


            while (true) {
                BulkRequest bulkRequest = new BulkRequest();

                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(3000));
                int recordCount = records.count();
                logger.info("Records received: " + recordCount);

                for (ConsumerRecord<String, String> record : records) {
                    String recordId = extractIdFromMessage(record.value());
                    IndexRequest indexRequest = new IndexRequest(config.getIndexName())
                            .source(record.value(), XContentType.JSON)
                            .id(recordId);
                    bulkRequest.add(indexRequest);
                }
                if (bulkRequest.numberOfActions() > 0) {
                    BulkResponse bulkResponse = openSearchClient.bulk(bulkRequest, RequestOptions.DEFAULT);
                    logger.info("Inserted " + bulkResponse.getItems().length + " record(s)");

                    consumer.commitSync();
                    logger.info("Offset has been committed");
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);

        } catch (OpenSearchStatusException ignored) {
        }

    }
}
