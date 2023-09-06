package com.theofourniez.whatsappclone.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.theofourniez.whatsappclone.message.Message;
import jakarta.persistence.*;
import lombok.*;
import nl.martijndwars.webpush.Subscription;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "chat_users")
public class ChatUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @JsonIgnore
    private String password;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    @JsonIgnore
    private Set<ChatUser> friends = new HashSet<>();

    @OneToMany(mappedBy = "sender", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<Message> messagesSent = new ArrayList<>();

    @OneToMany(mappedBy = "recipient", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<Message> messagesReceived = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    private Subscription pushSubscription;

    public void addFriend(ChatUser newFriend) {
        if (this.friends.stream().noneMatch(newFriend::equals) && !this.equals(newFriend)) {
            friends.add(newFriend);
            newFriend.getFriends().add(this);
        }
    }

    public void removeFriend(ChatUser oldFriend) {
        friends.remove(oldFriend);
        oldFriend.getFriends().remove(this);
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptySet();
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ChatUser){
            return Objects.equals(this.id, ((ChatUser) obj).getId());
        }
        return false;
    }
}
