package com.theofourniez.whatsappclone.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ChatWebSocketConfiguration {

    private static final String WEBSOCKET_PATH = "/chat-ws";

    @Bean
    public ChatWebSocketHandler serverHandler() {
        return new ChatWebSocketHandler();
    }

    @Bean
    public HandlerMapping handlerMapping(ChatWebSocketHandler serverHandler) {
        Map<String, WebSocketHandler> handlerByPathMap = new HashMap<>();
        handlerByPathMap.put(WEBSOCKET_PATH, serverHandler);

        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setUrlMap(handlerByPathMap);
        handlerMapping.setOrder(-1);

        return handlerMapping;
    }

    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}