package org.sergk.xchangestream.deribit;

import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingTradeService;
import io.reactivex.Completable;
import io.reactivex.Observable;
import org.knowm.xchange.deribit.v2.DeribitExchange;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;

public class DeribitStreamingExchange extends DeribitExchange implements StreamingExchange {

  private static final String API_URI = "wss://feed.exchange.coinjar.com/socket/websocket";

  private DeribitStreamingService streamingService;
  private DeribitStreamingMarketDataService streamingMarketDataService;
  private DeribitStreamingTradeService streamingTradeService;

  @Override
  protected void initServices() {
    throw new NotYetImplementedForExchangeException();
 }

  @Override
  public Completable connect(ProductSubscription... args) {
    return streamingService.connect();
  }

  @Override
  public Completable disconnect() {
    return streamingService.disconnect();
  }

  @Override
  public boolean isAlive() {
    return streamingService.isSocketOpen();
  }

  @Override
  public Observable<Throwable> reconnectFailure() {
    return streamingService.subscribeReconnectFailure();
  }

  @Override
  public Observable<Object> connectionSuccess() {
    return streamingService.subscribeConnectionSuccess();
  }

  @Override
  public DeribitStreamingMarketDataService getStreamingMarketDataService() {
    return streamingMarketDataService;
  }

  @Override
  public StreamingTradeService getStreamingTradeService() {
    return streamingTradeService;
  }

  @Override
  public void useCompressedMessages(boolean compressedMessages) {
    streamingService.useCompressedMessages(compressedMessages);
  }
}
