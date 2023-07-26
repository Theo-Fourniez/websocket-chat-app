package com.theofourniez.whatsappclone.message;

import com.theofourniez.whatsappclone.authentication.ChatUser;
import jakarta.persistence.*;

@Entity
public class Message {
    @Id
    @GeneratedValue
    private int id;
    private String content;

    @OneToOne(fetch = FetchType.EAGER)
    private ChatUser sender;

    @OneToOne(fetch = FetchType.EAGER)
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
