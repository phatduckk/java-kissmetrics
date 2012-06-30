package com.jeraff.kissmetrics.client;

import com.jeraff.kissmetrics.StatusOnlyAsyncHandler;
import com.ning.http.client.AsyncHttpClient;

import java.util.concurrent.Future;

public class KissMetricsClient {
    private String apiKey;
    private String id;
    private AsyncHttpClient httpClient;
    private boolean secure;
    private boolean useClientTimestamp;
    private Future<AsyncKissMetricsResponse> lastResponse;

    public static final String API_HOST = "trk.kissmetrics.com";

    public static final String PROP_API_KEY = "_k";
    public static final String PROP_EVENT_NAME = "_n";
    public static final String PROP_ALIAS_TO = "_n";
    public static final String PROP_IDENTITY = "_p";
    public static final String PROP_TIMESTAMP = "_t";
    public static final String PROP_USE_CLIENT_TIME = "_d";

    private static final String SCHEME_HTTS = "https";
    private static final String SCHEME_HTTP = "http";

    public static final String URL_FORMAT = "%s://%s%s?%s";

    public KissMetricsClient() {
    }

    public KissMetricsClient(String apiKey, String id) {
        this.apiKey = apiKey;
        this.id = id;
    }

    public KissMetricsClient(
            String apiKey, String id, AsyncHttpClient httpClient, boolean secure) {
        this.apiKey = apiKey;
        this.id = id;
        this.httpClient = httpClient;
        this.secure = secure;
    }

    public KissMetricsClient(String apiKey, String id, AsyncHttpClient httpClient) {
        this.apiKey = apiKey;
        this.id = id;
        this.httpClient = httpClient;
    }

    //////////////////////////////////////////////////////////////////////
    // API endpoints
    //////////////////////////////////////////////////////////////////////

    public KissMetricsClient record(String eventName, KissMetricsProperties properties)
            throws KissMetricsException {
        properties.putSafe(PROP_EVENT_NAME, eventName);
        call(ApiEndpoint.RECORD_EVENT, properties);
        return this;
    }

    public KissMetricsClient record(String eventName) throws KissMetricsException {
        record(eventName, new KissMetricsProperties());
        return this;
    }

    public KissMetricsClient set(KissMetricsProperties properties) throws KissMetricsException {
        call(ApiEndpoint.SET_PROPERTIES, properties);
        return this;
    }

    public KissMetricsClient alias(String aliasTo) throws KissMetricsException {
        final KissMetricsProperties props = new KissMetricsProperties().put(PROP_ALIAS_TO, aliasTo);
        call(ApiEndpoint.ALIAS_USER, props);
        return this;
    }

    //////////////////////////////////////////////////////////////////////
    // get the response...
    //////////////////////////////////////////////////////////////////////
    public KissMetricsResponse getResponse() throws KissMetricsException {
        final KissMetricsResponse kissMetricsResponse = new KissMetricsResponse();
        final AsyncKissMetricsResponse response;

        try {
            response = lastResponse.get();
            kissMetricsResponse.setStatus(response.getStatus());
        } catch (Exception e) {
            throw new KissMetricsException("couldn't get response", e);
        }

        return kissMetricsResponse;
    }

    //////////////////////////////////////////////////////////////////////
    // helpers
    //////////////////////////////////////////////////////////////////////

    public void call(ApiEndpoint endpoint, KissMetricsProperties properties)
            throws KissMetricsException {
        if (!isReady()) {
            throw new KissMetricsException();
        }

        properties.putSafe(PROP_API_KEY, apiKey).put(PROP_IDENTITY, id);

        if (useClientTimestamp) {
            properties.put(PROP_TIMESTAMP, (System.currentTimeMillis() / 1000L))
                      .put(PROP_USE_CLIENT_TIME, 1);
        }

        final String url = constructUrl(endpoint, properties);
        if (httpClient == null) {
            httpClient = new AsyncHttpClient();
        }

        try {
            lastResponse = httpClient.prepareGet(url).execute(new StatusOnlyAsyncHandler());
        } catch (Exception e) {
            throw new KissMetricsException("error: " + url, e);
        }
    }

    public String constructUrl(ApiEndpoint endpoint, KissMetricsProperties properties)
            throws KissMetricsException {
        final String scheme = secure
                ? SCHEME_HTTS
                : SCHEME_HTTP;

        try {
            final String s = String
                    .format(URL_FORMAT, scheme, API_HOST, endpoint.path(), properties.getQueryString());
            return s;
        } catch (Exception e) {
            throw new KissMetricsException("couldn't create uri", e);
        }
    }

    public boolean isReady() {
        return id != null && apiKey != null;
    }

    //////////////////////////////////////////////////////////////////////
    // getters and setters
    //////////////////////////////////////////////////////////////////////

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSecure() {
        return secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public boolean isUseClientTimestamp() {
        return useClientTimestamp;
    }

    public void setUseClientTimestamp(boolean useClientTimestamp) {
        this.useClientTimestamp = useClientTimestamp;
    }

    public AsyncHttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(AsyncHttpClient httpClient) {
        this.httpClient = httpClient;
    }
}
