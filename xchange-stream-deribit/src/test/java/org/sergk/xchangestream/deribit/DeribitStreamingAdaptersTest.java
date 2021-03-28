package org.sergk.xchangestream.deribit;

import com.fasterxml.jackson.databind.JsonNode;
import info.bitrich.xchangestream.service.netty.StreamingObjectMapperHelper;
import org.junit.Assert;
import org.junit.Test;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.sergk.xchangestream.deribit.DeribitStreamingAdapters;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author sergi-ko
 */
public class DeribitStreamingAdaptersTest {

    private static final CurrencyPair BTC_USD = new CurrencyPair(Currency.BTC, Currency.USD);

    @Test
    public void testJsonNodeAdaptOrderbookMessageWithSnapshotAndUpdate() throws IOException {
        JsonNode jsonNode =
                StreamingObjectMapperHelper.getObjectMapper()
                        .readTree(this.getClass().getResource("/subscription-book.ETH-PERPETUAL.100ms-snapshot.json").openStream());

        OrderBook beforeUpdate = new OrderBook(null, new ArrayList<>(), new ArrayList<>());
        Assert.assertNotNull(jsonNode);
        OrderBook afterInitialLoad = DeribitStreamingAdapters.adaptOrderbookMessage(beforeUpdate, BTC_USD, jsonNode.get("params").get("data"));

        Date initialDate = new Date(Long.parseLong("1616841026613"));
        Assert.assertEquals(initialDate, afterInitialLoad.getTimeStamp());

        Assert.assertEquals(551, afterInitialLoad.getAsks().size());
        Assert.assertEquals(775, afterInitialLoad.getBids().size());

        BigDecimal firstAskLimitPrice = afterInitialLoad.getAsks().get(0).getLimitPrice();
        BigDecimal firstAskLimitPriceShould = new BigDecimal("1694.35");
        Assert.assertEquals(firstAskLimitPriceShould, firstAskLimitPrice);
        Assert.assertEquals(new BigDecimal(226517), afterInitialLoad.getAsks().get(0).getOriginalAmount());

        Assert.assertEquals(new BigDecimal("1694.2"), afterInitialLoad.getBids().get(0).getLimitPrice());
        Assert.assertEquals(new BigDecimal(47), afterInitialLoad.getBids().get(0).getOriginalAmount());


        LimitOrder lastAskLimitOrder = afterInitialLoad.getAsks().get(afterInitialLoad.getAsks().size() - 1);
        Assert.assertEquals(0, lastAskLimitOrder.getLimitPrice().compareTo(new BigDecimal("5000")));
        Assert.assertEquals(0, lastAskLimitOrder.getOriginalAmount().compareTo(new BigDecimal(6434)));

        LimitOrder lastBidLimitOrder = afterInitialLoad.getBids().get(afterInitialLoad.getBids().size() - 1);
        Assert.assertEquals(0, lastBidLimitOrder.getLimitPrice().compareTo(new BigDecimal("152")));
        Assert.assertEquals(0, lastBidLimitOrder.getOriginalAmount().compareTo(new BigDecimal(40)));


        JsonNode jsonNodeUpdate =
                StreamingObjectMapperHelper.getObjectMapper()
                        .readTree(this.getClass().getResource("/subscription-book.ETH-PERPETUAL.100ms-change.json").openStream());

        OrderBook afterUpdate = DeribitStreamingAdapters.adaptOrderbookMessage(afterInitialLoad, BTC_USD, jsonNodeUpdate.get("params").get("data"));

        Date afterUpdateDate = new Date(Long.parseLong("1616841026914"));
        Assert.assertEquals(afterUpdateDate, afterUpdate.getTimeStamp());

        firstAskLimitPrice = afterUpdate.getAsks().get(0).getLimitPrice();
        firstAskLimitPriceShould = new BigDecimal("1694.35");
        Assert.assertEquals(firstAskLimitPriceShould, firstAskLimitPrice);
        Assert.assertEquals(new BigDecimal(208997), afterUpdate.getAsks().get(0).getOriginalAmount());

        Assert.assertEquals(new BigDecimal("1694.2"), afterUpdate.getBids().get(0).getLimitPrice());
        Assert.assertEquals(new BigDecimal(47), afterUpdate.getBids().get(0).getOriginalAmount());


        lastAskLimitOrder = afterUpdate.getAsks().get(afterUpdate.getAsks().size() - 1);
        Assert.assertEquals(0, lastAskLimitOrder.getLimitPrice().compareTo(new BigDecimal("5000")));
        Assert.assertEquals(0, lastAskLimitOrder.getOriginalAmount().compareTo(new BigDecimal(6434)));

        lastBidLimitOrder = afterUpdate.getBids().get(afterUpdate.getBids().size() - 1);
        Assert.assertEquals(0, lastBidLimitOrder.getLimitPrice().compareTo(new BigDecimal("152")));
        Assert.assertEquals(0, lastBidLimitOrder.getOriginalAmount().compareTo(new BigDecimal(40)));

        Assert.assertEquals(549, afterInitialLoad.getAsks().size());
        Assert.assertEquals(775, afterInitialLoad.getBids().size());

    }

    @Test
    public void testAdaptTrade() throws IOException {
        JsonNode jsonNode =
                StreamingObjectMapperHelper.getObjectMapper()
                        .readTree(this.getClass().getResource("/subscription-trades.ETH-PERPETUAL.100ms-single-trade.json").openStream());

        Trade trade = DeribitStreamingAdapters.adaptTrade(null, jsonNode);

        Assert.assertEquals("ETH-53743624", trade.getId());
        Assert.assertEquals(new BigDecimal(1), trade.getOriginalAmount());
        Assert.assertEquals(new Date(Long.parseLong("1616957704958")), trade.getTimestamp());
        Assert.assertEquals(Order.OrderType.ASK, trade.getType());
        Assert.assertEquals(new BigDecimal("1675.6"), trade.getPrice());
        Assert.assertNull(trade.getInstrument());
    }

    @Test
    public void testAdaptTrades() throws IOException {
        JsonNode jsonNode =
                StreamingObjectMapperHelper.getObjectMapper()
                        .readTree(this.getClass().getResource("/subscription-trades.ETH-PERPETUAL.100ms.json").openStream());

        List<Trade> trades = DeribitStreamingAdapters.adaptTrades(BTC_USD, jsonNode.get("params").get("data"));

        Assert.assertEquals(3, trades.size());

        Assert.assertEquals("ETH-53743624", trades.get(0).getId());
        Assert.assertEquals("ETH-53743625", trades.get(1).getId());
        Assert.assertEquals("ETH-53743626", trades.get(2).getId());
    }


    @Test
    public void testAdaptTickerMessage() throws IOException {
        JsonNode jsonNode =
                StreamingObjectMapperHelper.getObjectMapper()
                        .readTree(this.getClass().getResource("/subscription-ticker.ETH-PERPETUAL.100ms.json").openStream());

        Ticker ticker = DeribitStreamingAdapters.adaptTickerMessage(BTC_USD, jsonNode.get("params").get("data"));

        Assert.assertEquals(BTC_USD, ticker.getInstrument());
        Assert.assertEquals(new BigDecimal(141057220), ticker.getOpen());
        Assert.assertEquals(new BigDecimal("1670.3"), ticker.getAsk());
        Assert.assertEquals(new BigDecimal("534"), ticker.getAskSize());
        Assert.assertEquals(new BigDecimal("1670.1"), ticker.getBid());
        Assert.assertEquals(new BigDecimal("17776"), ticker.getBidSize());
        Assert.assertEquals(new BigDecimal("1670.35"), ticker.getLast());
        Assert.assertEquals(new BigDecimal("1735.1"), ticker.getHigh());
        Assert.assertEquals(new BigDecimal("1663.3"), ticker.getLow());
        Assert.assertEquals(new BigDecimal("72758.458464"), ticker.getVolume());
        Assert.assertEquals(new BigDecimal("124116154"), ticker.getQuoteVolume());
        Assert.assertEquals(new BigDecimal("-2.0552"), ticker.getPercentageChange());
        Assert.assertEquals(new Date(Long.parseLong("1616962111916")), ticker.getTimestamp());
    }

}
