package org.sergk.xchangestream.deribit;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Streams;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.instrument.Instrument;
import org.sergk.xchangestream.deribit.dto.DeribitWholeOrderBookSubscriptionNotificationData;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author sergi-ko
 */
public class DeribitStreamingAdapters {

    public static final String ENTRY_NEW = "new";

    public static final String NOTIFICATION_TYPE_SNAPSHOT = "snapshot";
    public static final String NOTIFICATION_TYPE_CHANGE = "change";

    public static OrderBook adaptOrderbookMessage(
            OrderBook orderBook,
            Instrument instrument,
            DeribitWholeOrderBookSubscriptionNotificationData msgObj) {
        String notificationType = msgObj.getType().name();
        Long timestamp = msgObj.getTimestamp();

        List<List<Object>> arrayNodeBids = msgObj.getBids();
        List<List<Object>> arrayNodeAsks = msgObj.getAsks();

//        if (NOTIFICATION_TYPE_SNAPSHOT.equals(notificationType)) {
//            orderBook = new OrderBook(
//                    new Date(timestamp),
//                    Streams.stream(arrayNodeAsks)
//                            .filter(node -> node.get(0).toString().equals(ENTRY_NEW))
//                            .map(node -> createLimitOrder(instrument, timestamp, node, Order.OrderType.ASK)
//                            ),
//                    Streams.stream(arrayNodeBids)
//                            .filter(node -> node.get(0).toString().equals(ENTRY_NEW))
//                            .map(node -> createLimitOrder(instrument, timestamp, node, Order.OrderType.BID)
//                            ),
//                    true
//            );
//        }
        if (NOTIFICATION_TYPE_CHANGE.equals(notificationType) || NOTIFICATION_TYPE_SNAPSHOT.equals(notificationType)) {
            arrayNodeAsks.stream()
                    .map(node -> createLimitOrder(instrument, timestamp, node, Order.OrderType.ASK))
                    .forEach(orderBook::update);

            arrayNodeBids.stream()
                    .map(node -> createLimitOrder(instrument, timestamp, node, Order.OrderType.BID))
                    .forEach(orderBook::update);
        }

        return orderBook;
    }

    private static LimitOrder createLimitOrder(Instrument instrument, long timestamp, List<Object> node, Order.OrderType bid) {
        return new LimitOrder(
                bid,
                new BigDecimal(node.get(2).toString()).stripTrailingZeros(),
                instrument,
                null,
                new Date(timestamp),
                new BigDecimal(node.get(1).toString()).stripTrailingZeros());
    }

    @SuppressWarnings("UnstableApiUsage")
    public static OrderBook adaptOrderbookMessage(OrderBook orderBook, Instrument instrument, JsonNode jsonMessage) {
        String notificationType = jsonMessage.get("params").get("data").get("type").textValue();
        long timestamp = jsonMessage.get("params").get("data").get("timestamp").asLong();

        JsonNode arrayNodeBids = jsonMessage.get("params").get("data").get("bids");
        JsonNode arrayNodeAsks = jsonMessage.get("params").get("data").get("asks");

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

}
