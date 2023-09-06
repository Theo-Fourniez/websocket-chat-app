package com.theofourniez.whatsappclone;

import org.apache.tomcat.websocket.AuthenticationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

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
            System.out.println(e.getCause().getCause());
            Assertions.assertTrue(e.getCause().getCause() instanceof AuthenticationException);
        }

    }

    @Test
    @WithUserDetails
    public void testWebSocketConnection_WithAuthentication() throws Exception {
        String url = getWebSocketUri();

        StandardWebSocketClient webSocketClient = new StandardWebSocketClient();

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("Authorization", "Basic dGVzdG1hbjpwYXNzd29yZA==");
        webSocketClient.setUserProperties(properties);

       WebSocketSession session = webSocketClient.execute(
                new TextWebSocketHandler() {
                    private final CountDownLatch latch = new CountDownLatch(1);

                    @Override
                    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
                        // WebSocket connection has been established
                        latch.countDown();
                    }

                    @Override
                    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
                        String receivedMessage = message.getPayload();
                        // Handle received message and perform assertions
                    }

                    @Override
                    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
                        // Handle transport errors
                    }

                    public boolean await() throws InterruptedException {
                        return latch.await(5, TimeUnit.SECONDS);
                    }
                },
        url
        ).get();

       session.close();

    }
}
