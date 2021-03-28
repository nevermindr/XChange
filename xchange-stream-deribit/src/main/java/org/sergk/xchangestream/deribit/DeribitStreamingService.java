package org.sergk.xchangestream.deribit;

import com.fasterxml.jackson.databind.JsonNode;
import info.bitrich.xchangestream.service.netty.JsonNettyStreamingService;
import org.sergk.xchangestream.deribit.dto.DeribitSubscriptionMessage;
import org.sergk.xchangestream.deribit.dto.DeribitSubscriptionMessageParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author sergi-ko
 */
class DeribitStreamingService extends JsonNettyStreamingService {

    private static final Logger logger =
            LoggerFactory.getLogger(DeribitStreamingService.class);


    private final AtomicInteger refCount = new AtomicInteger();

    public DeribitStreamingService(String apiUrl) {
        super(apiUrl);
    }

    @Override
    protected String getChannelNameFromMessage(JsonNode message) {
        String channel;
        if (message.has("method")) {
            channel = message.get("params").get("channel").toString();
        } else {
            channel = message.get("result").get(0).toString();
        }
        return channel.replaceAll("\"", "");
    }

    @Override
    public String getSubscribeMessage(String channelName, Object... args) throws IOException {
        int reqId = 1;
        String subscribeMethod = "public/subscribe";

        DeribitSubscriptionMessage subscriptionMessage =
                new DeribitSubscriptionMessage(
                        reqId,
                        subscribeMethod,
                        new DeribitSubscriptionMessageParams(channelName));

        String msg = objectMapper.writeValueAsString(subscriptionMessage);
        logger.info("msg" + msg);
        return msg;
    }

    @Override
    public String getUnsubscribeMessage(String channelName) throws IOException {
        int reqId = 2;
        String subscribeMethod = "public/unsubscribe";

        DeribitSubscriptionMessage unsubscriptionMessage =
                new DeribitSubscriptionMessage(
                        reqId,
                        subscribeMethod,
                        new DeribitSubscriptionMessageParams(channelName));

        String msg = objectMapper.writeValueAsString(unsubscriptionMessage);
        logger.info("msg" + msg);
        return msg;
    }

    @Override
    protected void handleMessage(JsonNode message) {
        String channelName = getChannel(message);

        logger.debug(channelName);

        super.handleMessage(message);
    }
}
