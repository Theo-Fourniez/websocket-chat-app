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

    @PostMapping("/unsubscribe")
    public ResponseEntity<Object> unsubscribe() {
        ChatUser senderUser =
                (ChatUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        pushMessageService.unsubscribe(senderUser);

        return new ResponseEntity<>("""
        {
          "message": "Unsubscribed",
        }
        """, HttpStatus.OK);
    }

    @GetMapping("/test-subscription")
    public ResponseEntity<String> testSubscription() {
        ChatUser user =
                (ChatUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(user.getPushSubscription() != null){
            System.out.println(user.getUsername() + " has subscription " + user.getPushSubscription().keys.auth);
        } else{
            System.out.println(user.getUsername() + " has no subscription");
        }
        var json = """
        {
            "notification": {
                "title": "Angular News",
                "body": "Newsletter Available!",
                "icon": "assets/main-page-logo-small-hat.png",
                "vibrate": [100, 50, 100],
                "data": {
                    "test": "lol"
                },
                "actions": [{
                    "action": "explore",
                    "title": "Go to the site"
                }]
            }
        }
        """;

        pushMessageService.sendNotification(user.getPushSubscription(), new PushNotification("Test " +
                "notification !", "This is in the body", "assets/icons/icon-72x72.png", null,null, null,
                null));

        return new ResponseEntity<>("Sent test message", HttpStatus.OK);
    }
}
