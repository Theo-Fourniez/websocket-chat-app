package com.theofourniez.whatsappclone.authentication;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class ChatUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String springUsername;

    @OneToMany(fetch = FetchType.EAGER)
    private List<ChatUser> friends;

    @Transient
    public void addFriend(ChatUser newFriend){
        if(this.friends.stream().noneMatch(newFriend::equals)){
            this.friends.add(newFriend);
        }
    }

    @Transient
    public void removeFriend(ChatUser oldFriend){
        if(this.friends.stream().anyMatch(oldFriend::equals)){
            this.friends.remove(oldFriend);
        }
    }

    public ChatUser(Long id, String springUsername, List<ChatUser> friends) {
        this.id = id;
        this.springUsername = springUsername;
        this.friends = friends;
    }

    @Transient
    public static ChatUser fromSpringUser(User user){
        return new ChatUser(null, user.getUsername(), new ArrayList<>());
    }
}
