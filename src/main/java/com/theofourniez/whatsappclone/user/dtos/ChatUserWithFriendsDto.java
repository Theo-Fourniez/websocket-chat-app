package com.theofourniez.whatsappclone.user.dtos;

import com.theofourniez.whatsappclone.user.ChatUser;
import lombok.Getter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class ChatUserWithFriendsDto {
    private final String username;
    private final Set<FriendDto> friends;

    public ChatUserWithFriendsDto(ChatUser user, Set<ChatUser> friends) {
        this.username = user.getUsername();
        this.friends = friends.stream().map(FriendDto::from).collect(Collectors.toSet());
    }

}
