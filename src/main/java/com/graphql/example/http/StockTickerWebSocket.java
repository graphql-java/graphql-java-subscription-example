package com.graphql.example.http;

import com.graphql.example.http.data.StockPriceUpdate;
import com.graphql.example.http.data.StockTickerPublisher;
import com.graphql.example.http.utill.JsonKit;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public class StockTickerWebSocket extends WebSocketAdapter {

    private static final Logger log = LoggerFactory.getLogger(StockTickerWebSocket.class);

    private final static StockTickerPublisher STOCK_TICKER_PUBLISHER = new StockTickerPublisher();


    private AtomicReference<Subscription> subscriptionRef = new AtomicReference<>();

    public StockTickerWebSocket() {
    }

    private Publisher<StockPriceUpdate> getPublisher() {
        return STOCK_TICKER_PUBLISHER.getPublisher();
    }

    @Override
    public void onWebSocketConnect(Session session) {
        super.onWebSocketConnect(session);

        getPublisher().subscribe(new Subscriber<StockPriceUpdate>() {

            @Override
            public void onSubscribe(Subscription s) {
                subscriptionRef.set(s);
                request(1);
            }

            @Override
            public void onNext(StockPriceUpdate stockPriceUpdate) {
                request(1);
                log.debug("Sending stick price update");
                try {
                    getRemote().sendString(JsonKit.toJsonString(stockPriceUpdate));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable t) {
                log.error("Subscription threw an exception", t);
                session.close();
            }

            @Override
            public void onComplete() {
                log.info("Subscription complete");
                session.close();
            }
        });
    }

    private void request(int n) {
        Subscription subscription = subscriptionRef.get();
        if (subscription != null) {
            subscription.request(n);
        }
    }

    @Override
    public void onWebSocketText(String message) {
        super.onWebSocketText(message);
        log.info("Websocket said {}", message);
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        log.info("Closing web socket");
        super.onWebSocketClose(statusCode, reason);
        Subscription subscription = subscriptionRef.get();
        if (subscription != null) {
            subscription.cancel();
        }
    }
}
