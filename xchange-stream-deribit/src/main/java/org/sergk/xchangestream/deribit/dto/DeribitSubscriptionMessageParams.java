package org.sergk.xchangestream.deribit.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sergi-ko
 */
public class DeribitSubscriptionMessageParams {

    @JsonProperty(value = "channels")
    private final List<String> channels;

    @JsonCreator
    public DeribitSubscriptionMessageParams(String channel) {
        this.channels = new ArrayList<>();
        this.channels.add(channel);
    }

    @JsonCreator
    public DeribitSubscriptionMessageParams(
            @JsonProperty("channels") List<String> channels) {
        this.channels = channels;
    }

}
