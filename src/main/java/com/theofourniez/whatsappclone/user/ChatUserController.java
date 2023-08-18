package com.theofourniez.whatsappclone.user;

import com.theofourniez.whatsappclone.user.dtos.ChatUserDto;
import com.theofourniez.whatsappclone.user.dtos.ChatUserWithFriendsDto;
import com.theofourniez.whatsappclone.user.dtos.FriendsDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/user")
class ChatUserController {
    private final ChatUserDetailsService chatUserDetailsService;
    public ChatUserController(ChatUserDetailsService chatUserDetailsService) {
        this.chatUserDetailsService = chatUserDetailsService;
    }

    @GetMapping(params = "withoutFriends")
    public ResponseEntity<ChatUserDto> getUser() {
        ChatUser user = (ChatUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(new ChatUserDto(user));
    }

    @GetMapping
    public ResponseEntity<ChatUserWithFriendsDto> getFullUser() {
        ChatUser user = (ChatUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user = chatUserDetailsService.loadUserWithFriendsByUsername(user.getUsername());

        ChatUserWithFriendsDto userWithFriendsDto =
                new ChatUserWithFriendsDto(user, user.getFriends());
        return ResponseEntity.ok(userWithFriendsDto);
    }

    private record PostFriendRequest(String username){}
    @PostMapping(path = "/friends")
    public ResponseEntity<Set<ChatUser>> postFriend(@RequestBody PostFriendRequest req) {
        ChatUser user = (ChatUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // We need to reload the user with the friends field populated
        user = chatUserDetailsService.loadUserWithFriendsByUsername(user.getUsername());

        ChatUser friendToAdd = chatUserDetailsService.loadUserByUsername(req.username);
        user.addFriend(friendToAdd);
        chatUserDetailsService.save(user);
        return ResponseEntity.ok(user.getFriends());

    }

    @GetMapping("/friends")
    public ResponseEntity<FriendsDto> getFriends() {
        ChatUser user = (ChatUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        FriendsDto friendsDto =
                new FriendsDto(chatUserDetailsService.loadUserWithFriendsByUsername(user.getUsername()).getFriends());
        return ResponseEntity.ok(friendsDto);
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logged out successfully");
    }
}