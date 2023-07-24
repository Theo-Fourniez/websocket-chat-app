package com.theofourniez.whatsappclone.authentication;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
public class ChatUser extends User {
    @Id
    @GeneratedValue
    private Long id;

    @OneToMany
    private List<ChatUser> friends;

    public ChatUser(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, Long id) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.id = id;
        this.friends = new ArrayList<>();
    }

    public ChatUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.friends = new ArrayList<>();

    }

    public ChatUser(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    public Long getId() {
        return id;
    }

    public void addFriend(ChatUser newFriend){
        if(this.friends.stream().noneMatch(newFriend::equals)){
            this.friends.add(newFriend);
        }
    }

    public void removeFriend(ChatUser oldFriend){
        if(this.friends.stream().anyMatch(oldFriend::equals)){
            this.friends.remove(oldFriend);
        }
    }
}
