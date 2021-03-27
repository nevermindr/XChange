package org.sergk.xchangestream.deribit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author sergi-ko
 */
public class DeribitSubscriptionNotification<T> {

    @JsonProperty
    private final String method;
    @JsonProperty("jsonrpc")
    private final String jsonrpc = "2.0";

    @JsonProperty("params")
    private final DeribitSubscriptionNotificationParams<T> params;

    private String channel;
    private T data;

    public DeribitSubscriptionNotification(
            @JsonProperty("method") String method,
            @JsonProperty("params") DeribitSubscriptionNotificationParams<T> params) {
        this.method = method;
        this.params = params;
        this.channel = params.getChannel();
        this.data = params.getData();
    }

    public String getMethod() {
        return method;
    }

    public String getChannel() {
        return channel;
    }

    public T getData() {
        return data;
    }
}


class DeribitSubscriptionNotificationParams<T> {

    private String channel;
    private T data;

    public DeribitSubscriptionNotificationParams(
            @JsonProperty("channel") String channel,
            @JsonProperty("data") T data) {
        this.channel = channel;
        this.data = data;
    }

    public String getChannel() {
        return channel;
    }

    public T getData() {
        return data;
    }
}

