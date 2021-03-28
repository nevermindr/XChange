package org.sergk.xchangestream.deribit;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Streams;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.instrument.Instrument;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author sergi-ko
 */
public class DeribitStreamingAdapters {
    public static final String NOTIFICATION_TYPE_SNAPSHOT = "snapshot";
    public static final String NOTIFICATION_TYPE_CHANGE = "change";

    @SuppressWarnings("UnstableApiUsage")
    public static OrderBook adaptOrderbookMessage(OrderBook orderBook, Instrument instrument, JsonNode dataNode) {
        String notificationType = dataNode.get("type").textValue();
        long timestamp = dataNode.get("timestamp").asLong();

        JsonNode arrayNodeBids = dataNode.get("bids");
        JsonNode arrayNodeAsks = dataNode.get("asks");

        if (NOTIFICATION_TYPE_CHANGE.equals(notificationType) || NOTIFICATION_TYPE_SNAPSHOT.equals(notificationType)) {
            Streams.stream(arrayNodeAsks.elements())
                    .map(node -> createLimitOrder(instrument, timestamp, node, Order.OrderType.ASK))
                    .forEach(orderBook::update);

            Streams.stream(arrayNodeBids.elements())
                    .map(node -> createLimitOrder(instrument, timestamp, node, Order.OrderType.BID))
                    .forEach(orderBook::update);
        }
        return orderBook;
    }

    private static LimitOrder createLimitOrder(Instrument instrument, long timestamp, JsonNode node, Order.OrderType bid) {
        return new LimitOrder(
                bid,
                getVolumeFromWholeOrderBookNotificationEntry(node),
                instrument,
                null,
                new Date(timestamp),
                getPriceFromWholeOrderBookNotificationEntry(node));
    }

    private static BigDecimal getPriceFromWholeOrderBookNotificationEntry(JsonNode node) {
        return new BigDecimal(node.get(1).asText()).stripTrailingZeros();
    }

    private static BigDecimal getVolumeFromWholeOrderBookNotificationEntry(JsonNode node) {
        return new BigDecimal(node.get(2).asText()).stripTrailingZeros();
    }

    /**
     * Adapt an JsonNode into a list of Trade
     */
    @SuppressWarnings("UnstableApiUsage")
    public static List<Trade> adaptTrades(Instrument instrument, JsonNode dataArrayNode) {
        return Streams.stream(dataArrayNode.elements())
                .map(innerNode -> DeribitStreamingAdapters.adaptTrade(instrument, innerNode))
                .collect(Collectors.toList());
    }

    /**
     * Adapt an JsonNode into a single Trade
     */
    public static Trade adaptTrade(Instrument instrument, JsonNode arrayNode) {
        if (arrayNode == null) {
            return null;
        }
        return new Trade.Builder()
                .price(getNodeAsBigDecimal(arrayNode, "price"))
                .originalAmount(getNodeAsBigDecimal(arrayNode, "amount"))
                .timestamp(getNodeAsTimestamp(arrayNode.get("timestamp").asLong()))
                .type(arrayNode.get("amount").asText().equals("buy") ? Order.OrderType.BID : Order.OrderType.ASK)
                .instrument(instrument)
                .id(arrayNode.get("trade_id").asText())
                .build();
    }

    /**
     * Adapt an ArrayNode containing a ticker message into a Ticker
     */
    public static Ticker adaptTickerMessage(Instrument instrument, JsonNode dataNode) {
        return new Ticker.Builder()
                .open(getNodeAsBigDecimal(dataNode, "open_interest"))
                .ask(getNodeAsBigDecimal(dataNode, "best_ask_price"))
                .askSize(getNodeAsBigDecimal(dataNode, "best_ask_amount"))
                .bid(getNodeAsBigDecimal(dataNode, "best_bid_price"))
                .bidSize(getNodeAsBigDecimal(dataNode, "best_bid_amount"))
                .last(getNodeAsBigDecimal(dataNode, "last_price"))
                .high(getNodeAsBigDecimal(dataNode.get("stats"), "high"))
                .low(getNodeAsBigDecimal(dataNode.get("stats"), "low"))
//                            .vwap(nextNodeAsDecimal(vwapIterator))
                .volume(getNodeAsBigDecimal(dataNode.get("stats"), "volume"))
                .quoteVolume(getNodeAsBigDecimal(dataNode.get("stats"), "volume_usd"))
                .percentageChange(getNodeAsBigDecimal(dataNode.get("stats"), "price_change"))
                .timestamp(getNodeAsTimestamp(dataNode.get("timestamp").asLong()))
                .instrument(instrument)
                .build();
    }

    private static BigDecimal getNodeAsBigDecimal(JsonNode arrayNode, String price) {
        return new BigDecimal(arrayNode.get(price).asText());
    }

    private static Date getNodeAsTimestamp(long timestamp) {
        return new Date(timestamp);
    }
}
