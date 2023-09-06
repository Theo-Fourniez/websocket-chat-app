package com.theofourniez.whatsappclone.websocket;

import com.theofourniez.whatsappclone.message.MessageService;
import com.theofourniez.whatsappclone.pushnotifications.PushMessageService;
import com.theofourniez.whatsappclone.user.ChatUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

    private final ChatUserDetailsService chatUserDetailsService;
    private final MessageService messageService;
    private final PushMessageService pushMessageService;

    public WebSocketConfiguration(ChatUserDetailsService chatUserDetailsService,
                                  MessageService messageService, PushMessageService pushMessageService) {
        this.chatUserDetailsService = chatUserDetailsService;
        this.messageService = messageService;
        this.pushMessageService = pushMessageService;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(myHandler(), "/chat-ws").setAllowedOrigins("*").addInterceptors(handshakeInterceptor());
    }

    @Bean
    public WebSocketHandler myHandler() {
        return new ChatWebSocketHandler(chatUserDetailsService, messageService, pushMessageService);
    }

    public HttpSessionHandshakeInterceptor handshakeInterceptor() {
        return new HttpSessionHandshakeInterceptor();
    }



}