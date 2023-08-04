package com.theofourniez.whatsappclone.message.dtos;

import com.theofourniez.whatsappclone.message.Message;
import com.theofourniez.whatsappclone.user.ChatUser;

import java.time.Instant;
import java.util.List;

public class MessageDto {
    public String content;
    public Instant createdAt;

    public boolean areYouTheSender;

    public MessageDto(String content, Instant createdAt, boolean areYouTheSender) {
        this.content = content;
        this.createdAt = createdAt;
        this.areYouTheSender = areYouTheSender;
    }

    public static List<MessageDto> fromMessages(List<Message> messages, ChatUser senderUser) {
        return messages.stream().map(message -> new MessageDto(message.getContent(), message.getCreatedOn()
                , message.getSender().getUsername().equals(senderUser.getUsername()))).toList();
    }
}
