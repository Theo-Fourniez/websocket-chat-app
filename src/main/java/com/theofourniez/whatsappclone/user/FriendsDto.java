package com.theofourniez.whatsappclone.user;

import java.util.List;
import java.util.Set;

public class FriendsDto {
    public List<Friend> friends;
    record Friend(String username) {
    }

    public FriendsDto(Set<ChatUser> friends) {
        this.friends = friends.stream().map(ChatUser::getUsername).map(Friend::new).toList();
    }
}
