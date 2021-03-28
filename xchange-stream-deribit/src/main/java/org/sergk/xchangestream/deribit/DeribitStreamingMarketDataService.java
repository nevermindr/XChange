package org.sergk.xchangestream.deribit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import info.bitrich.xchangestream.service.netty.StreamingObjectMapperHelper;
import io.reactivex.Observable;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trade;
import org.sergk.xchangestream.deribit.dto.DeribitSubscriptionName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author sergi-ko
 */
class DeribitStreamingMarketDataService implements StreamingMarketDataService {

    private static final Logger logger =
            LoggerFactory.getLogger(DeribitStreamingMarketDataService.class);

    private final DeribitStreamingService service;

    public DeribitStreamingMarketDataService(DeribitStreamingService service) {
        this.service = service;
    }

    @Override
    public Observable<OrderBook> getOrderBook(CurrencyPair currencyPair, Object... args) {
        String channelName = String.format("%s.ETH-PERPETUAL.100ms", DeribitSubscriptionName.book);
        OrderBook orderBook = new OrderBook(null, Lists.newArrayList(), Lists.newArrayList());
        return subscribe(channelName)
                .filter(node -> node.has("method") && node.get("method").asText().equals("subscription"))
                .filter(node -> node.has("params") && node.get("params").get("channel").asText().equals(channelName))
                .map(node ->
                        DeribitStreamingAdapters.adaptOrderbookMessage(
                                orderBook,
                                currencyPair,
                                node.get("params").get("data")
                        )
                );
    }


    @Override
    public Observable<Ticker> getTicker(CurrencyPair currencyPair, Object... args) {
        String channelName = String.format("%s.ETH-PERPETUAL.100ms", DeribitSubscriptionName.ticker);
        return subscribe(channelName)
                .filter(node -> node.has("method") && node.get("method").asText().equals("subscription"))
                .filter(node -> node.has("params") && node.get("params").get("channel").asText().equals(channelName))
                .map(node ->
                        DeribitStreamingAdapters.adaptTickerMessage(
                                currencyPair,
                                node.get("params").get("data")
                        )
                );
    }

    @Override
    public Observable<Trade> getTrades(CurrencyPair currencyPair, Object... args) {
        String channelName = String.format("%s.ETH-PERPETUAL.100ms", DeribitSubscriptionName.trades);
        return subscribe(channelName)
                .filter(node -> node.has("method") && node.get("method").asText().equals("subscription"))
                .filter(node -> node.has("params") && node.get("params").get("channel").asText().equals(channelName))
                .flatMap(node ->
                        Observable.fromIterable(DeribitStreamingAdapters.adaptTrades(
                                currencyPair,
                                node.get("params").get("data"))
                        )
                );
    }

    public Observable<JsonNode> subscribe(String channelName) {
        return service
                .subscribeChannel(channelName);
    }

}
