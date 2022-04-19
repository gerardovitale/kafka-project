package gve.kafka.opensearch;


import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestClientBuilder;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.client.indices.CreateIndexRequest;
import org.opensearch.client.indices.GetIndexRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;

public class OpenSearchClient extends RestHighLevelClient {

    private static final Logger logger = LoggerFactory.getLogger(OpenSearchClient.class.getSimpleName());

    private OpenSearchClient(RestClientBuilder restClientBuilder) {
        super(restClientBuilder);
    }

    public static OpenSearchClient createOpenSearchClient(ConfigReader config) {
        URI connUri = URI.create(config.getOpensearchServer());
        String userInfo = connUri.getUserInfo();

        if (userInfo == null) {
            return new OpenSearchClient(RestClient.builder(
                    new HttpHost(connUri.getHost(), connUri.getPort(), "http")));
        }

        String[] auth = userInfo.split(":");
        CredentialsProvider cp = new BasicCredentialsProvider();
        cp.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(auth[0], auth[1]));

        return new OpenSearchClient(
                RestClient.builder(new HttpHost(connUri.getHost(), connUri.getPort(), connUri.getScheme()))
                        .setHttpClientConfigCallback(
                                httpAsyncClientBuilder -> httpAsyncClientBuilder
                                        .setDefaultCredentialsProvider(cp)
                                        .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())));


    }

    public void createIndex() throws IOException {
        boolean indexExists = indices().exists(new GetIndexRequest("wikimedia"), RequestOptions.DEFAULT);

        if (!indexExists) {
            CreateIndexRequest createIndexRequest = new CreateIndexRequest("wikimedia");
            indices().create(createIndexRequest, RequestOptions.DEFAULT);
            logger.info("The Wikimedia Index has been created!");

        } else {
            logger.info("The Wikimedia Index already exits");
        }
    }
}
