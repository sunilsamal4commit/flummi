package de.otto.flummi.request;

import de.otto.flummi.response.HttpServerErrorException;
import de.otto.flummi.util.HttpClientWrapper;
import org.asynchttpclient.Response;
import org.slf4j.Logger;

import java.util.concurrent.ExecutionException;

import static de.otto.flummi.request.RequestConstants.APPL_JSON;
import static de.otto.flummi.request.RequestConstants.CONTENT_TYPE;
import static org.slf4j.LoggerFactory.getLogger;

public class IndicesExistsRequestBuilder implements RequestBuilder<Boolean> {
    private final String indexName;

    public static final Logger LOG = getLogger(IndicesExistsRequestBuilder.class);
    private HttpClientWrapper httpClient;

    public IndicesExistsRequestBuilder(HttpClientWrapper httpClient, String indexName) {
        this.indexName = indexName;
        this.httpClient = httpClient;
    }

    public Boolean execute() {
        try {
            Response response = httpClient.prepareHead("/" + indexName)
                    .addHeader(CONTENT_TYPE, APPL_JSON)
                    .execute().get();
            int statusCode = response.getStatusCode();
            if (statusCode >= 300 && response.getStatusCode() != 404) {
                throw new HttpServerErrorException(response.getStatusCode(), response.getStatusText(), response.getResponseBody());
            }
            return statusCode < 300;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
