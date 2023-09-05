package com.theofourniez.whatsappclone.message;

import com.theofourniez.whatsappclone.message.dtos.MessageDto;
import com.theofourniez.whatsappclone.user.ChatUser;
import com.theofourniez.whatsappclone.user.ChatUserDetailsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
class MessageController {

    private final MessageService messageService;
    private final ChatUserDetailsService chatUserDetailsService;

    public MessageController(MessageService messageService, ChatUserDetailsService      chatUserDetailsService) {
        this.messageService = messageService;
        this.chatUserDetailsService = chatUserDetailsService;
    }

    @GetMapping("/{username}")
    public ResponseEntity<List<MessageDto>> getMessagesWith(@PathVariable String username) {
        ChatUser senderUser =
                (ChatUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ChatUser recipientUser = chatUserDetailsService.loadUserByUsername(username);
        List<Message> messages = messageService.getConversationBetween(senderUser, recipientUser);
        return new ResponseEntity<>(MessageDto.fromMessages(messages, senderUser), HttpStatus.OK);
    }
}