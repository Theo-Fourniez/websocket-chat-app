package com.theofourniez.whatsappclone.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theofourniez.whatsappclone.message.Message;
import com.theofourniez.whatsappclone.message.MessageService;
import com.theofourniez.whatsappclone.user.ChatUser;
import com.theofourniez.whatsappclone.user.ChatUserDetailsService;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

// TODO : Add a special message type to send to the client from the server
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final List<ConnectedUser> currentConnectedUsers = new ArrayList<ConnectedUser>();

    private final ChatUserDetailsService chatUserDetailsService;
    private final MessageService messageService;
    public ChatWebSocketHandler(ChatUserDetailsService chatUserDetailsService, MessageService messageService) {
        this.chatUserDetailsService = chatUserDetailsService;
        this.messageService = messageService;
    }


    /// A record used to keep all the current users connected via websocket
    private class ConnectedUser {
        protected ChatUser user;
        protected WebSocketSession session;

        public ConnectedUser(ChatUser user, WebSocketSession session) {
            this.user = user;
            this.session = session;
        }
    }

    /// A record used to validate incoming messages from users as JSON
    private record SendMessageRequest(String message, String to) {}

    public SendMessageRequest parseStringToMessageRequest(String message) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(message, SendMessageRequest.class);
    }

    private record SendMessageResponse(String message, String from) {}

    @SneakyThrows
    public String convertMessageResponseToString(SendMessageResponse message) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(message);
    }

    private Stream<ConnectedUser> getAllUsersExcept(ChatUser currentUser){
        return currentConnectedUsers
                .stream()
                .filter(connectedUser -> !connectedUser.user.getUsername().equals(currentUser.getUsername()))
                ;
    }

    // Websockets methods
    @Override
    @Transactional
    public void handleTextMessage(WebSocketSession rawSession, TextMessage message) throws IOException {
        // Decorate the session to make it thread safe
        ConcurrentWebSocketSessionDecorator webSocketSession =
                new ConcurrentWebSocketSessionDecorator(rawSession, 10000, 1024*1024);

        // Get the current user from the session
        ConnectedUser currentConnectedUser = currentConnectedUsers.stream().filter(u -> {
            return u.session.equals(rawSession);
        }).findFirst().orElseThrow(() -> new RuntimeException("Session not found"));
        logger.debug("User["+ currentConnectedUser.user.getUsername()+"] " + " is sending a message");

        // Parse the message he sent as JSON
        SendMessageRequest sendMessageRequest;
        try {
            sendMessageRequest = parseStringToMessageRequest(message.getPayload());
        }catch (JsonProcessingException e){
            logger.error("User["+ currentConnectedUser.user.getUsername()+"] " + "Could not parse JSON: " + message.getPayload());
            webSocketSession.sendMessage(new TextMessage(convertMessageResponseToString(new SendMessageResponse(
                    "Could not " +
                    "parse JSON", "Server"))));
            return;
        }

        // Update the user to update his friends list
        currentConnectedUser.user =
                chatUserDetailsService.loadUserWithFriendsByUsername(currentConnectedUser.user.getUsername());

        // Retrieve the friend he wants to send the message to
        Optional<ChatUser> friendToSendTo =
                currentConnectedUser.user.getFriends().stream().filter(friend -> {
                    return friend.getUsername().equals(sendMessageRequest.to);
                }).findFirst();

        // The user does not have this user as friend
        if(friendToSendTo.isEmpty()){
            webSocketSession.sendMessage(new TextMessage(convertMessageResponseToString(new SendMessageResponse(
                    sendMessageRequest.to + " is not in your friendlist", "Server"
            ))));
            return;
        }

        // We check if the friend is connected
        // If he is, we send him the message directly
        // If he is not, we save the message in the database
        currentConnectedUsers.stream().filter(connectedUser -> {
            return connectedUser.user.getUsername().equals(sendMessageRequest.to);
        }).findFirst().ifPresentOrElse(connectedFriend -> {
            try {
                connectedFriend.session.sendMessage(new TextMessage(convertMessageResponseToString(new SendMessageResponse(
                        sendMessageRequest.message, currentConnectedUser.user.getUsername()))));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        },() -> {
            Message messageToSave = new Message(sendMessageRequest.message,currentConnectedUser.user,friendToSendTo.get());
            messageService.save(messageToSave);
        });
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        super.afterConnectionEstablished(webSocketSession);

        logger.debug("Session["+webSocketSession.getId()+"] " + "Connection established");

        Authentication authentication = (Authentication) webSocketSession.getPrincipal();
        ChatUser senderUser = (ChatUser) authentication.getPrincipal();
        senderUser = chatUserDetailsService.loadUserWithFriendsByUsername(senderUser.getUsername());

        logger.debug("Session["+webSocketSession.getId()+"] " + "User connected: " + senderUser.getUsername());

        ConnectedUser currentSession = new ConnectedUser(senderUser, webSocketSession);

        // We firstly check if the user has already opened a session
        boolean sessionAlreadyOpened = currentConnectedUsers.stream().anyMatch(connectedUser -> {
            return connectedUser.user.equals(currentSession.user);
        });

        // A user cannot be connected multiple times, so we disconnect him from the socket
        if(sessionAlreadyOpened){
            logger.debug("Session["+webSocketSession.getId()+"] " + senderUser.getUsername() + " has already another websocket opened, closing this one");
            webSocketSession.close(new CloseStatus(3000, "User has already opened a websocket session"));
            return;
        }

        // We keep all current sessions in a list
        currentConnectedUsers.add(currentSession);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus status) throws Exception {
        super.afterConnectionClosed(webSocketSession, status);
        logger.debug("Session["+webSocketSession.getId()+"] " + "Connection closed");
        // We remove the user from the current sessions list when he closes the connection
        currentConnectedUsers.stream().filter(connectedUser -> {
            return connectedUser.session.equals(webSocketSession);
        }).findFirst().ifPresent(connectedUser -> {
            logger.debug("Session["+webSocketSession.getId()+"] " + connectedUser.user.getUsername() + " removed from the current websocket sessions");
            currentConnectedUsers.remove(connectedUser);
        });
    }
}
