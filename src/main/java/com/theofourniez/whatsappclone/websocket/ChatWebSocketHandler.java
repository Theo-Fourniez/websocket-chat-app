package com.theofourniez.whatsappclone.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theofourniez.whatsappclone.authentication.ChatUser;
import com.theofourniez.whatsappclone.authentication.ChatUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.reactive.socket.CloseStatus;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ChatWebSocketHandler implements WebSocketHandler {

    public ChatWebSocketHandler() {
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final List<UserSession> currentSessions = new ArrayList<UserSession>();

    @Autowired
    private ChatUserService chatUserService;

    private record UserSession(UserDetails user, WebSocketSession session) {}

    private Stream<UserSession> getAllUsersExcept(User currentUser){
        return currentSessions
                .stream()
                .filter(userSession -> !userSession.user.getUsername().equals(currentUser.getUsername()))
                ;
    }

    private record SendMessageRequest(String message, String to) {}
    public SendMessageRequest parseStringToMessageRequest(String message) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(message, SendMessageRequest.class);
    }

    public Mono<Void> sendMessageToSession(WebSocketSession session, String message){
        return session.send(Mono.just(session.textMessage(message)));
    }

    public Mono<Void> handle(WebSocketSession senderSession) {
        return ReactiveSecurityContextHolder.getContext().map(
                SecurityContext::getAuthentication
        ).flatMap(
                authentication -> {
                    User senderUser = (User) authentication.getPrincipal();

                    Optional<ChatUser> maybeChatUser =
                            chatUserService.findBySpringUsername(senderUser.getUsername());

                    ChatUser sendingChatUser;

                    if(maybeChatUser.isEmpty()){
                        ChatUser newChatUser = ChatUser.fromSpringUser(senderUser);
                        chatUserService.save(newChatUser);
                        sendingChatUser = newChatUser;
                    }else{
                        sendingChatUser = maybeChatUser.get();
                    }

                    logger.debug("Session["+senderSession.getId()+"] " + senderUser.getUsername() + " wants to chat");
                    UserSession currentSession = new UserSession(senderUser, senderSession);

                    // We firstly check if the user has already opened a session
                    boolean sessionAlreadyOpened = currentSessions.stream().anyMatch(userSession -> {
                        return userSession.user.equals(currentSession.user);
                    });

                    // A user cannot be connected multiple times
                    if(sessionAlreadyOpened){
                        logger.debug("Session["+senderSession.getId()+"] " + senderUser.getUsername() + " has already another websocket opened, closing this one");
                        return senderSession.close(new CloseStatus(3000, "User has already opened a websocket session"));
                    }

                    currentSessions.add(currentSession);
                    logger.debug("Session["+senderSession.getId()+"] " + senderUser.getUsername() + " added to the current websocket sessions, starting processing");

                    return senderSession.
                            receive()
                            .map(WebSocketMessage::getPayloadAsText)
                            .flatMap(rawString -> {
                                try {
                                    SendMessageRequest messageRequest = parseStringToMessageRequest(rawString);
                                    return Flux.just(messageRequest);
                                } catch (JsonProcessingException e) {
                                    return sendMessageToSession(senderSession, "Request not " +
                                            "understood " +
                                            "by " +
                                            "the server");
                                }
                            })
                            .flatMap(message -> {
                                return Flux.defer(() -> Flux.fromStream(getAllUsersExcept(senderUser))) //
                                        // for now get all other users and broadcast the message
                                        .flatMap(userToSendTo -> {
                                            if(message instanceof SendMessageRequest req){
                                                return sendMessageToSession(userToSendTo.session,
                                                        senderUser.getUsername() + " : " + req.message);
                                            }

                                            return sendMessageToSession(senderSession,
                                                    "SERVER" + " : " + "Didn't understand your request");
                                        });
                            }).then(Mono.defer(() -> {
                                currentSessions.remove(currentSession);
                                logger.debug("Session["+senderSession.getId()+"] " + senderUser.getUsername() + " removed from active sessions");
                                return Mono.empty();
                    }));
                }

        );
    }
}