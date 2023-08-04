package com.theofourniez.whatsappclone;

import org.apache.tomcat.websocket.AuthenticationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChatWebSocketTests {

    @LocalServerPort
    private int port;


    public class ChatWebSocketHandler extends TextWebSocketHandler {
        @Override
        public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
            System.out.println(message.getPayload());
        }
    }

    public String getWebSocketUri() {
        return "ws://localhost:" + port + "/chat-ws";
    }

    @Test
    /**
     * Supposed to fail because no authentication is provided
     */
    public void testConnect_WithoutAuthentication() throws InterruptedException {
        StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
        TextWebSocketHandler webSocketHandler =
                new ChatWebSocketHandler();

        try{
            WebSocketSession session = webSocketClient.execute(webSocketHandler, getWebSocketUri()).get();
        }catch (ExecutionException e){
            Assertions.assertTrue(e.getCause().getCause() instanceof AuthenticationException);
        }

    }
}
