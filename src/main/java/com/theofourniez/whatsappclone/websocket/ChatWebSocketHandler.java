package com.theofourniez.whatsappclone.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.theofourniez.whatsappclone.message.Message;
import com.theofourniez.whatsappclone.message.MessageService;
import com.theofourniez.whatsappclone.pushnotifications.PushMessageService;
import com.theofourniez.whatsappclone.pushnotifications.PushNotification;
import com.theofourniez.whatsappclone.user.ChatUser;
import com.theofourniez.whatsappclone.user.ChatUserDetailsService;
import com.theofourniez.whatsappclone.websocket.dtos.SendMessageRequest;
import com.theofourniez.whatsappclone.websocket.dtos.SendMessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
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
// TODO : refactor the code to make it cleaner
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final List<UserSession> currentConnectedUsers = new ArrayList<UserSession>();
    private PushMessageService pushMessageService;

    private final ChatUserDetailsService chatUserDetailsService;
    private final MessageService messageService;
    public ChatWebSocketHandler(ChatUserDetailsService chatUserDetailsService,
                                MessageService messageService, PushMessageService pushMessageService) {
        this.chatUserDetailsService = chatUserDetailsService;
        this.messageService = messageService;
        this.pushMessageService = pushMessageService;
    }


    /// A record used to keep all the current users connected via websocket
    private class UserSession {
        protected ChatUser user;
        protected WebSocketSession session;

        public UserSession(ChatUser user, WebSocketSession session) {
            this.user = user;
            this.session = session;
        }
    }

    private Stream<UserSession> getAllUsersExcept(ChatUser currentUser){
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
        UserSession currentConnectedUser = currentConnectedUsers.stream().filter(u -> {
            return u.session.equals(rawSession);
        }).findFirst().orElseThrow(() -> new RuntimeException("Session not found"));
        logger.debug("User["+ currentConnectedUser.user.getUsername()+"] " + " is sending a message");

        // Parse the message the client sent as JSON
        SendMessageRequest sendMessageRequest;
        try {
            sendMessageRequest = SendMessageRequest.parse(message.getPayload());
        }catch (JsonProcessingException e){
            logger.error("User["+ currentConnectedUser.user.getUsername()+"] " + "Could not parse JSON: " + message.getPayload());
            webSocketSession.sendMessage(new TextMessage(new SendMessageResponse(
                    "Could not " +
                    "parse JSON", "Server").toString()));
            return;
        }

        // Update the user to update his friends list
        currentConnectedUser.user =
                chatUserDetailsService.loadUserWithFriendsByUsername(currentConnectedUser.user.getUsername());

        // Retrieve the friend he wants to send the message to
        Optional<ChatUser> friendToSendTo =
                currentConnectedUser.user.getFriends().stream().filter(friend -> {
                    return friend.getUsername().equals(sendMessageRequest.to());
                }).findFirst();

        // The user does not have this user as friend
        if(friendToSendTo.isEmpty()){
            webSocketSession.sendMessage(new TextMessage(new SendMessageResponse(
                    sendMessageRequest.to() + " is not in your friendlist", "Server"
            ).toString()));
            return;
        }

        // We check if the friend is connected
        // If he is, we send him the message directly
        currentConnectedUsers.stream().filter(connectedUser -> {
            return connectedUser.user.getUsername().equals(sendMessageRequest.to());
        }).findFirst().ifPresentOrElse(connectedFriend -> {
            try {
                connectedFriend.session.sendMessage(new TextMessage(new SendMessageResponse(
                        sendMessageRequest.message(), currentConnectedUser.user.getUsername()).toString()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, () -> {
            // If he is not, we send him a push notification
            System.out.println("Sending push notification to " + friendToSendTo.get().getUsername());
            var upToDateFriend =
                    chatUserDetailsService.loadUserByUsername(friendToSendTo.get().getUsername());
            try {
                pushMessageService.sendNotification(upToDateFriend.getPushSubscription(), new PushNotification(
                        "\uD83D\uDD34 New message from " + currentConnectedUser.user.getUsername(),
                        sendMessageRequest.message(), "assets/icons/whatsapp-icon-64x64.png",
                        currentConnectedUser.user.getUsername(),
                        null,
                        new JSONObject().put(
                                "onActionClick",
                                new JSONObject().put("open-message", new JSONObject().put("operation",
                                        "focusLastFocusedOrOpen").put("url",
                                        "http://localhost:4200/"))).put("username", currentConnectedUser.user.getUsername()),
                        List.of(new JSONObject().put("action", "open-message").put("title",
                                "Go to conversation"))));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });


        // In every case, we save the message in the database to be able to retrieve it later
        Message messageToSave = new Message(sendMessageRequest.message(),currentConnectedUser.user,
                friendToSendTo.get());
        messageService.save(messageToSave);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        super.afterConnectionEstablished(webSocketSession);

        logger.debug("Session["+webSocketSession.getId()+"] " + "Connection established");

        Authentication authentication = (Authentication) webSocketSession.getPrincipal();
        ChatUser senderUser = (ChatUser) authentication.getPrincipal();
        senderUser = chatUserDetailsService.loadUserWithFriendsByUsername(senderUser.getUsername());

        logger.debug("Session["+webSocketSession.getId()+"] " + "User connected: " + senderUser.getUsername());

        UserSession currentSession = new UserSession(senderUser, webSocketSession);

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
