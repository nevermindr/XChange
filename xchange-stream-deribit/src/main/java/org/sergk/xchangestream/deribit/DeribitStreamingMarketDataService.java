package org.sergk.xchangestream.deribit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
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
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;
import org.sergk.xchangestream.deribit.dto.DeribitSubscriptionName;
import org.sergk.xchangestream.deribit.dto.DeribitSubscriptionNotification;
import org.sergk.xchangestream.deribit.dto.DeribitWholeOrderBookSubscriptionNotificationData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static info.bitrich.xchangestream.service.netty.StreamingObjectMapperHelper.getObjectMapper;

/**
 * @author sergi-ko
 */
class DeribitStreamingMarketDataService implements StreamingMarketDataService {

    private static final Logger logger =
            LoggerFactory.getLogger(DeribitStreamingMarketDataService.class);

    private final ObjectMapper mapper = StreamingObjectMapperHelper.getObjectMapper();

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
                .map(node -> {
                    DeribitSubscriptionNotification<DeribitWholeOrderBookSubscriptionNotificationData> msgObj =
                            mapper.readValue(mapper.treeAsTokens(node), getDepthType());
                    return DeribitStreamingAdapters.adaptOrderbookMessage(orderBook, currencyPair, msgObj.getData());
                });
//                .map(node -> {
//                    return DeribitStreamingAdapters.adaptOrderbookMessage(orderBook, currencyPair, node);
//                });
    }

    private static JavaType getDepthType() {
        return getObjectMapper()
                .getTypeFactory()
                .constructType(
                        new TypeReference<DeribitSubscriptionNotification<DeribitWholeOrderBookSubscriptionNotificationData>>() {
                        });
    }

    @Override
    public Observable<Ticker> getTicker(CurrencyPair currencyPair, Object... args) {
        throw new NotYetImplementedForExchangeException();
    }

    @Override
    public Observable<Trade> getTrades(CurrencyPair currencyPair, Object... args) {
        throw new NotYetImplementedForExchangeException();
    }

    public Observable<JsonNode> subscribe(String channelName) {
        return service
                .subscribeChannel(channelName);
    }

}
