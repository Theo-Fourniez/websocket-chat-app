package com.theofourniez.whatsappclone.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.reactive.socket.CloseStatus;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class ChatWebSocketHandler implements WebSocketHandler {

    public ChatWebSocketHandler() {
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final List<UserSession> currentSessions = new ArrayList<UserSession>();

    private record UserSession(User user, WebSocketSession session) {}

    public Mono<Void> handle(WebSocketSession session) {
        return ReactiveSecurityContextHolder.getContext().map(
                SecurityContext::getAuthentication
        ).flatMap(
                authentication -> {
                    User user = (User)authentication.getPrincipal();
                    logger.debug("Session["+session.getId()+"] " + user.getUsername() + " wants to initiate WebSocket");
                    UserSession currentSession = new UserSession(user, session);

                    // We firstly check if the user has already opened a session
                    boolean sessionAlreadyOpened = this.currentSessions.stream().anyMatch(userSession -> {
                        return userSession.user.equals(currentSession.user);
                    });

                    if(sessionAlreadyOpened){
                        logger.debug("Session["+session.getId()+"] " + user.getUsername() + " has already another websocket opened, closing this one");
                        return session.close(new CloseStatus(3000, "User has already opened a websocket session"));
                    }

                    this.currentSessions.add(currentSession);
                    logger.debug("Session["+session.getId()+"] " + user.getUsername() + " added to the current websocket sessions, starting processing");


                    return new ChatWebSocketLogic().doLogic(session).then(Mono.defer(() -> {
                        currentSessions.remove(currentSession);
                        logger.debug("Session["+session.getId()+"] " + user.getUsername() + " removed from active sessions");
                        return Mono.empty();
                    }));
                }
        );
    }
}