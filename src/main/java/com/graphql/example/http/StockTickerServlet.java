package com.graphql.example.http;

import java.time.Duration;
import org.eclipse.jetty.websocket.server.JettyWebSocketServlet;
import org.eclipse.jetty.websocket.server.JettyWebSocketServletFactory;

/**
 * In Jetty this is how you map a servlet to a websocket per request
 */
public class StockTickerServlet extends JettyWebSocketServlet {
    
    @Override
    protected void configure(JettyWebSocketServletFactory factory) {
        factory.setMaxTextMessageSize(1024 * 1024);
        factory.setIdleTimeout(Duration.ofSeconds(30));
        factory.register(StockTickerWebSocket.class);
    }
}


