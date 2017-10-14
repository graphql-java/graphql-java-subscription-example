package com.graphql.example.http;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class StockTickerServlet extends WebSocketServlet {
    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.getPolicy().setMaxTextMessageBufferSize(1024*1024);
        factory.getPolicy().setIdleTimeout(30 * 1000);
        factory.register(StockTickerWebSocket.class);
    }
}


