package gve.kafka.wikimedia;

import com.launchdarkly.shaded.com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.shaded.com.launchdarkly.eventsource.EventSource;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.concurrent.TimeUnit;

public class WikimediaProducer {

    private static final Logger logger = LoggerFactory.getLogger(WikimediaProducer.class.getSimpleName());

    public static void main(String[] args) throws InterruptedException {
        ConfigReader config = new ConfigReader();

        KafkaProducer<String, String> producer = ProducerFactory.createKafkaProducer(config);

        EventHandler eventHandler = new WikimediaChangeHandler(producer, config.getTopic());
        EventSource.Builder builder = new EventSource.Builder(eventHandler, URI.create(config.getUrl()));
        EventSource eventSource = builder.build();
        eventSource.start();

        TimeUnit.MINUTES.sleep(10);
    }
}
