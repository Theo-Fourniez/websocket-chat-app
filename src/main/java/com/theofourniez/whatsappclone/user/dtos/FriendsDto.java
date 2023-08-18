package com.theofourniez.whatsappclone.user.dtos;

import com.theofourniez.whatsappclone.user.ChatUser;

import java.util.List;
import java.util.Set;

public class FriendsDto {
    public List<FriendDto> friends;


    public FriendsDto(Set<ChatUser> friends) {
        this.friends = friends.stream().map(FriendDto::from).toList();
    }
}
