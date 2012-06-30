package com.jeraff.kissmetrics.client;

import java.util.HashMap;

public class AsyncKissMetricsResponse extends KissMetricsResponse {

    public AsyncKissMetricsResponse(int status) {
        super(status, new HashMap<String, String>());
    }
}
