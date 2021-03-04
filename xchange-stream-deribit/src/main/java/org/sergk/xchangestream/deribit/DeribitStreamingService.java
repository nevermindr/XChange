package org.sergk.xchangestream.deribit;

import com.fasterxml.jackson.databind.JsonNode;
import info.bitrich.xchangestream.service.netty.JsonNettyStreamingService;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

class DeribitStreamingService extends JsonNettyStreamingService {

  private final AtomicInteger refCount = new AtomicInteger();

  private String apiKey;

  public DeribitStreamingService(String apiUrl, String apiKey) {
    super(apiUrl);
    this.apiKey = apiKey;
    throw new NotYetImplementedForExchangeException();
  }

  @Override
  protected String getChannelNameFromMessage(JsonNode message) {
    throw new NotYetImplementedForExchangeException();
  }

  @Override
  public String getSubscribeMessage(String channelName, Object... args) {
    throw new NotYetImplementedForExchangeException();
  }

  @Override
  public String getUnsubscribeMessage(String channelName) {
    throw new NotYetImplementedForExchangeException();
  }
}
