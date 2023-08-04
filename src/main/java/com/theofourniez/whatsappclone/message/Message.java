package com.theofourniez.whatsappclone.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.theofourniez.whatsappclone.user.ChatUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;

import java.time.Instant;

@Entity
@Table(name = "messages")
@Getter
@Setter
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;


    @CreationTimestamp(source = SourceType.VM)
    private Instant createdOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    @JsonManagedReference
    @JsonIgnore
    private ChatUser sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id")
    @JsonManagedReference // prevents infinite recursion
    @JsonIgnore
    private ChatUser recipient;

    public Message() {
    }

    public Message(String content, ChatUser sender, ChatUser recipient) {
        this.content = content;
        this.createdOn =  Instant.now();
        this.sender = sender;
        this.recipient = recipient;
    }

}
