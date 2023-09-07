package com.theofourniez.whatsappclone.pushnotifications;

import com.theofourniez.whatsappclone.user.ChatUser;
import com.theofourniez.whatsappclone.user.ChatUserDetailsService;
import jakarta.transaction.Transactional;
import nl.martijndwars.webpush.Subscription;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/push")
public class PushMessageController {

    private final PushMessageService pushMessageService;
    private final ChatUserDetailsService chatUserDetailsService;

    public PushMessageController(PushMessageService pushMessageService, ChatUserDetailsService chatUserDetailsService) {
        this.pushMessageService = pushMessageService;
        this.chatUserDetailsService = chatUserDetailsService;
    }
    @PostMapping(path = "/subscribe")
    @Transactional
    public ResponseEntity<Object> subscribe(@RequestBody Subscription subscription) {
        System.out.println("Received subscription from  for user " + subscription.keys.auth);
        ChatUser senderUser =
                (ChatUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        senderUser = pushMessageService.subscribe(senderUser, subscription);
        chatUserDetailsService.save(senderUser);
        System.out.println("Subscribed " + senderUser.getUsername() + " to " + senderUser.getPushSubscription().keys.auth);
        return new ResponseEntity<>("{\"message\": \"Subscribed\"}", HttpStatus.OK);
    }
}
