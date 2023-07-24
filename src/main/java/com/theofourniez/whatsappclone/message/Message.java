package com.theofourniez.whatsappclone.message;

import com.theofourniez.whatsappclone.authentication.ChatUser;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class Message {
    @Id
    @GeneratedValue
    private int id;
    private String content;

    @OneToOne
    private ChatUser sender;

    @OneToOne
    private ChatUser recipient;

    public Message() {
    }

    public Message(int id, String content, ChatUser sender, ChatUser recipient) {
        this.id = id;
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
