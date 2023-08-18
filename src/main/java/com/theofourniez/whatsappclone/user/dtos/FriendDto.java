package com.theofourniez.whatsappclone.user.dtos;

import com.theofourniez.whatsappclone.user.ChatUser;

public class FriendDto {
    public String username;

    public FriendDto(String username) {
        this.username = username;
    }

    public static FriendDto from(ChatUser friend){
        return new FriendDto(friend.getUsername());
    }
}
