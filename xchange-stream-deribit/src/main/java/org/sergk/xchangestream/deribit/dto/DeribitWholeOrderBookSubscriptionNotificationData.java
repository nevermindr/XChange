package org.sergk.xchangestream.deribit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author sergi-ko
 */
public class DeribitWholeOrderBookSubscriptionNotificationData {

    @JsonProperty
    private final String method;
    @JsonProperty("jsonrpc")
    private final String jsonrpc = "2.0";

    private String channel;

    private DeribitWholeOrderBookNotificationDataType type;
    private Long timestamp;
    private final BigDecimal prevChangeId;
    private final String instrumentName;
    private BigDecimal changeId;

    private List<List<Object>> bids;
    private List<List<Object>> asks;

    public DeribitWholeOrderBookSubscriptionNotificationData(
            @JsonProperty("method") String method,
            @JsonProperty("type") String type,
            @JsonProperty("timestamp") Long timestamp,
            @JsonProperty("prevChangeId") BigDecimal prevChangeId,
            @JsonProperty("instrumentName") String instrumentName,
            @JsonProperty("changeId") BigDecimal changeId,
            @JsonProperty("bids") List<List<Object>> bids,
            @JsonProperty("asks") List<List<Object>> asks
    ) {
        this.method = method;
        this.type = DeribitWholeOrderBookNotificationDataType.valueOf(type);
        this.timestamp = timestamp;
        this.prevChangeId = prevChangeId;
        this.instrumentName = instrumentName;
        this.changeId = changeId;
        this.bids = bids;
        this.asks = asks;
    }

    public String getMethod() {
        return method;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public String getChannel() {
        return channel;
    }

    public DeribitWholeOrderBookNotificationDataType getType() {
        return type;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public BigDecimal getChangeId() {
        return changeId;
    }

    public List<List<Object>> getBids() {
        return bids;
    }

    public List<List<Object>> getAsks() {
        return asks;
    }

    public BigDecimal getPrevChangeId() {
        return prevChangeId;
    }

    public String getInstrumentName() {
        return instrumentName;
    }

    public enum DeribitWholeOrderBookNotificationDataType {
        snapshot,
        change
    }
}


