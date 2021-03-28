package org.sergk.xchangestream.deribit;

import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.CurrencyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by sergi-ko on 26.03.21.
 */
public class DeribitTickerExample {
    private static final Logger LOG = LoggerFactory.getLogger(DeribitTickerExample.class);

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void main(String[] args) {
        ExchangeSpecification exchangeSpecification =
                new ExchangeSpecification(DeribitStreamingExchange.class);

        StreamingExchange exchange =
                StreamingExchangeFactory.INSTANCE.createExchange(exchangeSpecification);
        exchange.connect().blockingAwait();

        exchange
                .getStreamingMarketDataService()
                .getTicker(CurrencyPair.BTC_USD)
                .subscribe(
                        ticker -> LOG.info("Ticker: {}", ticker),
                        throwable -> LOG.error("ERROR in getting trades: ", throwable));
    }
}
