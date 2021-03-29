package org.sergk.xchangestream.deribit;

import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.derivative.FuturesContract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by sergi-ko on 26.03.21.
 */
public class DeribitAllExample {
    private static final Logger LOG = LoggerFactory.getLogger(DeribitAllExample.class);

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void main(String[] args) {
        ExchangeSpecification exchangeSpecification =
                new ExchangeSpecification(DeribitStreamingExchange.class);

        StreamingExchange exchange =
                StreamingExchangeFactory.INSTANCE.createExchange(exchangeSpecification);
        exchange.connect().blockingAwait();

        String futureSymbol = String.format("%s/%s/%s", "ETH", "USD", "perpetual");
        FuturesContract ETH_PERPETUAL = new FuturesContract(futureSymbol);

        exchange
                .getStreamingMarketDataService()
                .getOrderBook(CurrencyPair.ETH_USD, ETH_PERPETUAL)
                .subscribe(
                        orderBook -> {
                            LOG.info("Order Book size: {} {}", orderBook.getAsks().size(), orderBook.getBids().size());
                            LOG.info("First ask: {}", orderBook.getAsks().get(0));
                            LOG.info("First bid: {}", orderBook.getBids().get(0));
                        },
                        throwable -> LOG.error("ERROR in getting order book: ", throwable));

        exchange
                .getStreamingMarketDataService()
                .getTrades(CurrencyPair.ETH_USD, ETH_PERPETUAL)
                .subscribe(
                        trade -> LOG.info("TRADE: {}", trade),
                        throwable -> LOG.error("ERROR in getting trades: ", throwable));

        exchange
                .getStreamingMarketDataService()
                .getTicker(CurrencyPair.ETH_USD, ETH_PERPETUAL)
                .subscribe(
                        ticker -> LOG.info("Ticker: {}", ticker),
                        throwable -> LOG.error("ERROR in getting trades: ", throwable));
    }
}
