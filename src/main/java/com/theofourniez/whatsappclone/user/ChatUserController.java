package com.theofourniez.whatsappclone.user;

import com.theofourniez.whatsappclone.user.dtos.ChatUserDto;
import com.theofourniez.whatsappclone.user.dtos.FriendsDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
class ChatUserController {
    private final ChatUserDetailsService chatUserDetailsService;
    public ChatUserController(ChatUserDetailsService chatUserDetailsService) {
        this.chatUserDetailsService = chatUserDetailsService;
    }

    @GetMapping
    public ResponseEntity<ChatUserDto> getUser() {
        ChatUser user = (ChatUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(new ChatUserDto(user));
    }
    private record PostFriendRequest(String username){}
    @PostMapping(path = "/friends")
    public ResponseEntity<String> postFriend(PostFriendRequest req) {
        ChatUser user = (ChatUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // We need to reload the user with the friends field populated
        user = chatUserDetailsService.loadUserWithFriendsByUsername(user.getUsername());

        ChatUser friendToAdd = chatUserDetailsService.loadUserByUsername(req.username);
        user.addFriend(friendToAdd);
        chatUserDetailsService.save(user);
        return ResponseEntity.ok("Friend added successfully");

    }

    @GetMapping("/friends")
    public ResponseEntity<FriendsDto> getFriends() {
        ChatUser user = (ChatUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        FriendsDto friendsDto =
                new FriendsDto(chatUserDetailsService.loadUserWithFriendsByUsername(user.getUsername()).getFriends());
        return ResponseEntity.ok(friendsDto);
    }
}