package org.sergk.xchangestream.deribit.dto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.service.netty.StreamingObjectMapperHelper;
import org.junit.Assert;
import org.junit.Test;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.sergk.xchangestream.deribit.DeribitStreamingAdapters;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import static info.bitrich.xchangestream.service.netty.StreamingObjectMapperHelper.getObjectMapper;

/**
 * @author sergi-ko
 */
public class DeribitStreamingAdaptersTest {

    private static final CurrencyPair BTC_USD = new CurrencyPair(Currency.BTC, Currency.USD);

    private final ObjectMapper mapper = StreamingObjectMapperHelper.getObjectMapper();

    private static JavaType getDepthType() {
        return getObjectMapper()
                .getTypeFactory()
                .constructType(
                        new TypeReference<DeribitSubscriptionNotification<DeribitWholeOrderBookSubscriptionNotificationData>>() {});
    }

    @Test
    public void testJsonNodeAdaptOrderbookMessageWithSnapshotAndUpdate() throws IOException {
        JsonNode jsonNode =
                StreamingObjectMapperHelper.getObjectMapper()
                        .readTree(this.getClass().getResource("/subscription-book.ETH-PERPETUAL.100ms-snapshot.json").openStream());

        OrderBook beforeUpdate = new OrderBook(null, new ArrayList<>(), new ArrayList<>());
        Assert.assertNotNull(jsonNode);
        OrderBook afterInitialLoad = DeribitStreamingAdapters.adaptOrderbookMessage(beforeUpdate, BTC_USD, jsonNode);

        Date initialDate = new Date(Long.parseLong("1616841026613"));
        Assert.assertEquals(afterInitialLoad.getTimeStamp(), initialDate);

        Assert.assertEquals(afterInitialLoad.getAsks().size(), 551);
        Assert.assertEquals(afterInitialLoad.getBids().size(), 775);

        BigDecimal firstAskLimitPrice = afterInitialLoad.getAsks().get(0).getLimitPrice();
        BigDecimal firstAskLimitPriceShould = new BigDecimal("1694.35");
        Assert.assertEquals(firstAskLimitPrice, firstAskLimitPriceShould);
        Assert.assertEquals(afterInitialLoad.getAsks().get(0).getOriginalAmount(), new BigDecimal(226517));

        Assert.assertEquals(afterInitialLoad.getBids().get(0).getLimitPrice(), new BigDecimal("1694.2"));
        Assert.assertEquals(afterInitialLoad.getBids().get(0).getOriginalAmount(), new BigDecimal(47));


        LimitOrder lastAskLimitOrder = afterInitialLoad.getAsks().get(afterInitialLoad.getAsks().size() - 1);
        Assert.assertEquals(lastAskLimitOrder.getLimitPrice().compareTo(new BigDecimal("5000")), 0);
        Assert.assertEquals(lastAskLimitOrder.getOriginalAmount().compareTo(new BigDecimal(6434)), 0);

        LimitOrder lastBidLimitOrder = afterInitialLoad.getBids().get(afterInitialLoad.getBids().size() - 1);
        Assert.assertEquals(lastBidLimitOrder.getLimitPrice().compareTo(new BigDecimal("152")), 0);
        Assert.assertEquals(lastBidLimitOrder.getOriginalAmount().compareTo(new BigDecimal(40)), 0);


        JsonNode jsonNodeUpdate =
                StreamingObjectMapperHelper.getObjectMapper()
                        .readTree(this.getClass().getResource("/subscription-book.ETH-PERPETUAL.100ms-change.json").openStream());

        OrderBook afterUpdate = DeribitStreamingAdapters.adaptOrderbookMessage(afterInitialLoad, BTC_USD, jsonNodeUpdate);

        Date afterUpdateDate = new Date(Long.parseLong("1616841026914"));
        Assert.assertEquals(afterUpdate.getTimeStamp(), afterUpdateDate);

        firstAskLimitPrice = afterUpdate.getAsks().get(0).getLimitPrice();
        firstAskLimitPriceShould = new BigDecimal("1694.35");
        Assert.assertEquals(firstAskLimitPrice, firstAskLimitPriceShould);
        Assert.assertEquals(afterUpdate.getAsks().get(0).getOriginalAmount(), new BigDecimal(208997));

        Assert.assertEquals(afterUpdate.getBids().get(0).getLimitPrice(), new BigDecimal("1694.2"));
        Assert.assertEquals(afterUpdate.getBids().get(0).getOriginalAmount(), new BigDecimal(47));


        lastAskLimitOrder = afterUpdate.getAsks().get(afterUpdate.getAsks().size() - 1);
        Assert.assertEquals(lastAskLimitOrder.getLimitPrice().compareTo(new BigDecimal("5000")), 0);
        Assert.assertEquals(lastAskLimitOrder.getOriginalAmount().compareTo(new BigDecimal(6434)), 0);

        lastBidLimitOrder = afterUpdate.getBids().get(afterUpdate.getBids().size() - 1);
        Assert.assertEquals(lastBidLimitOrder.getLimitPrice().compareTo(new BigDecimal("152")), 0);
        Assert.assertEquals(lastBidLimitOrder.getOriginalAmount().compareTo(new BigDecimal(40)), 0);

        Assert.assertEquals(afterInitialLoad.getAsks().size(), 549);
        Assert.assertEquals(afterInitialLoad.getBids().size(), 775);

    }

    @Test
    public void testNotificationObjectAdaptOrderbookMessageWithSnapshotAndUpdate() throws IOException {
        JsonNode jsonNodeSnapshot =
                StreamingObjectMapperHelper.getObjectMapper()
                        .readTree(this.getClass().getResource("/subscription-book.ETH-PERPETUAL.100ms-snapshot.json").openStream());

        DeribitSubscriptionNotification<DeribitWholeOrderBookSubscriptionNotificationData> msgObj = mapper.readValue(mapper.treeAsTokens(jsonNodeSnapshot), getDepthType());

        OrderBook beforeUpdate = new OrderBook(null, new ArrayList<>(), new ArrayList<>());
        Assert.assertNotNull(jsonNodeSnapshot);
        OrderBook afterInitialLoad = DeribitStreamingAdapters.adaptOrderbookMessage(beforeUpdate, BTC_USD, msgObj.getData());

        Date initialDate = new Date(Long.parseLong("1616841026613"));
        Assert.assertEquals(afterInitialLoad.getTimeStamp(), initialDate);

        Assert.assertEquals(afterInitialLoad.getAsks().size(), 551);
        Assert.assertEquals(afterInitialLoad.getBids().size(), 775);

        BigDecimal firstAskLimitPrice = afterInitialLoad.getAsks().get(0).getLimitPrice();
        BigDecimal firstAskLimitPriceShould = new BigDecimal("1694.35");
        Assert.assertEquals(firstAskLimitPrice, firstAskLimitPriceShould);
        Assert.assertEquals(afterInitialLoad.getAsks().get(0).getOriginalAmount(), new BigDecimal(226517));

        Assert.assertEquals(afterInitialLoad.getBids().get(0).getLimitPrice(), new BigDecimal("1694.2"));
        Assert.assertEquals(afterInitialLoad.getBids().get(0).getOriginalAmount(), new BigDecimal(47));


        LimitOrder lastAskLimitOrder = afterInitialLoad.getAsks().get(afterInitialLoad.getAsks().size() - 1);
        Assert.assertEquals(lastAskLimitOrder.getLimitPrice().compareTo(new BigDecimal("5000")), 0);
        Assert.assertEquals(lastAskLimitOrder.getOriginalAmount().compareTo(new BigDecimal(6434)), 0);

        LimitOrder lastBidLimitOrder = afterInitialLoad.getBids().get(afterInitialLoad.getBids().size() - 1);
        Assert.assertEquals(lastBidLimitOrder.getLimitPrice().compareTo(new BigDecimal("152")), 0);
        Assert.assertEquals(lastBidLimitOrder.getOriginalAmount().compareTo(new BigDecimal(40)), 0);


        JsonNode jsonNodeUpdate =
                StreamingObjectMapperHelper.getObjectMapper()
                        .readTree(this.getClass().getResource("/subscription-book.ETH-PERPETUAL.100ms-change.json").openStream());

        DeribitSubscriptionNotification<DeribitWholeOrderBookSubscriptionNotificationData> msgObjUpdate = mapper.readValue(mapper.treeAsTokens(jsonNodeUpdate), getDepthType());

        OrderBook afterUpdate = DeribitStreamingAdapters.adaptOrderbookMessage(afterInitialLoad, BTC_USD, msgObjUpdate.getData());

        Date afterUpdateDate = new Date(Long.parseLong("1616841026914"));
        Assert.assertEquals(afterUpdate.getTimeStamp(), afterUpdateDate);

        firstAskLimitPrice = afterUpdate.getAsks().get(0).getLimitPrice();
        firstAskLimitPriceShould = new BigDecimal("1694.35");
        Assert.assertEquals(firstAskLimitPrice, firstAskLimitPriceShould);
        Assert.assertEquals(afterUpdate.getAsks().get(0).getOriginalAmount(), new BigDecimal(208997));

        Assert.assertEquals(afterUpdate.getBids().get(0).getLimitPrice(), new BigDecimal("1694.2"));
        Assert.assertEquals(afterUpdate.getBids().get(0).getOriginalAmount(), new BigDecimal(47));


        lastAskLimitOrder = afterUpdate.getAsks().get(afterUpdate.getAsks().size() - 1);
        Assert.assertEquals(lastAskLimitOrder.getLimitPrice().compareTo(new BigDecimal("5000")), 0);
        Assert.assertEquals(lastAskLimitOrder.getOriginalAmount().compareTo(new BigDecimal(6434)), 0);

        lastBidLimitOrder = afterUpdate.getBids().get(afterUpdate.getBids().size() - 1);
        Assert.assertEquals(lastBidLimitOrder.getLimitPrice().compareTo(new BigDecimal("152")), 0);
        Assert.assertEquals(lastBidLimitOrder.getOriginalAmount().compareTo(new BigDecimal(40)), 0);

        Assert.assertEquals(afterInitialLoad.getAsks().size(), 549);
        Assert.assertEquals(afterInitialLoad.getBids().size(), 775);

    }
}
