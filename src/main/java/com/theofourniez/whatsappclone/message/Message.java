package com.theofourniez.whatsappclone.message;

import com.theofourniez.whatsappclone.user.ChatUser;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String content;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = ChatUser.class)
    private ChatUser sender;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = ChatUser.class)
    private ChatUser recipient;

    @CreationTimestamp
    private Instant createdOn;

    public Message() {
    }

    public Message(String content, ChatUser sender, ChatUser recipient) {
        this.content = content;
        this.sender = sender;
        this.recipient = recipient;
    }

    public String getContent() {
        return content;
    }

    public int getId() {
        return id;
    }

}
