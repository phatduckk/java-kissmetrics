package com.jeraff.kissmetrics;

import com.jeraff.kissmetrics.client.AsyncKissMetricsResponse;
import com.ning.http.client.AsyncHandler;
import com.ning.http.client.HttpResponseBodyPart;
import com.ning.http.client.HttpResponseHeaders;
import com.ning.http.client.HttpResponseStatus;

public class StatusOnlyAsyncHandler implements AsyncHandler<AsyncKissMetricsResponse> {
    private int status;

    @Override
    public void onThrowable(Throwable t) {
        t.printStackTrace();
    }

    @Override
    public STATE onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception {
        return null;
    }

    @Override
    public STATE onStatusReceived(HttpResponseStatus responseStatus) throws Exception {
        this.status = responseStatus.getStatusCode();
        return STATE.ABORT;
    }

    @Override
    public STATE onHeadersReceived(HttpResponseHeaders headers) throws Exception {
        return null;
    }

    @Override
    public AsyncKissMetricsResponse onCompleted() throws Exception {
        return new AsyncKissMetricsResponse(this.status);
    }
}
