package com.theofourniez.whatsappclone.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatWebSocketLogic {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final AtomicBoolean newClient;

    public ChatWebSocketLogic() {
        newClient = new AtomicBoolean(true);
    }

    public Mono<Void> doLogic(WebSocketSession session) {
        return session
                .receive()
                .map(WebSocketMessage::getPayloadAsText)
                .flatMap(message -> sendAtInterval(session, 500))
                .then();
    }

    private Flux<Void> sendAtInterval(WebSocketSession session, long interval) {
        return Flux
                .interval(Duration.ofMillis(interval))
                .map(miliseconds -> Long.toString(miliseconds))
                .flatMap(stringMiliseconds -> session
                        .send(Mono.fromCallable(() -> session.textMessage(stringMiliseconds)))
                        .then(
                                Mono
                                        .fromRunnable(() -> logger.info("Server -> sent: [{}] to client id=[{}]",
                                                stringMiliseconds, session.getId()))));
    }
}