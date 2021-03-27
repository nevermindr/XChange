package org.sergk.xchangestream.deribit.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author sergi-ko
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeribitSubscriptionMessage {

    @JsonProperty
    private final Integer id;
    @JsonProperty
    private final String method;

    @JsonProperty("params")
    private final DeribitSubscriptionMessageParams params;

    @JsonProperty("jsonrpc")
    private final String jsonrpc = "2.0";

    @JsonCreator
    public DeribitSubscriptionMessage(
            @JsonProperty("id") Integer id,
            @JsonProperty("method") String method,
            @JsonProperty("params") DeribitSubscriptionMessageParams params) {
        this.id = id;
        this.method = method;
        this.params = params;
    }


    public DeribitSubscriptionMessageParams getParams() {
        return params;
    }

    public Integer getId() {
        return id;
    }
}
