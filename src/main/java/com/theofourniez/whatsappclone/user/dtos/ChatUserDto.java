package com.theofourniez.whatsappclone.user.dtos;

import com.theofourniez.whatsappclone.user.ChatUser;
import lombok.Getter;

@Getter
public class ChatUserDto {
    private final String username;

    public ChatUserDto(ChatUser user){
        this.username = user.getUsername();
    }

}
