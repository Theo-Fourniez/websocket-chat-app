package com.theofourniez.whatsappclone.user;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
class ChatUserController {
    private final ChatUserDetailsService chatUserDetailsService;
    public ChatUserController(ChatUserDetailsService chatUserDetailsService) {
        this.chatUserDetailsService = chatUserDetailsService;
    }

    private record PostFriendRequest(String username){}
    @PostMapping(path = "/friends")
    public ResponseEntity<String> postFriend(PostFriendRequest req) {
        ChatUser user = (ChatUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // We need to reload the user with the friends field populated
        user = chatUserDetailsService.loadUserWithFriendsByUsername(user.getUsername());

        try {
            ChatUser friendToAdd = chatUserDetailsService.loadUserByUsername(req.username);
            user.addFriend(friendToAdd);
            chatUserDetailsService.save(user);
            return ResponseEntity.ok("Friend added successfully");
        }catch (UsernameNotFoundException e){
            return ResponseEntity.badRequest().body("Username not found");
        }
    }

    @GetMapping("/friends")
    public ResponseEntity<FriendsDto> getFriends() {
        ChatUser user = (ChatUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        FriendsDto friendsDto =
                new FriendsDto(chatUserDetailsService.loadUserWithFriendsByUsername(user.getUsername()).getFriends());
        return ResponseEntity.ok(friendsDto);
    }
}