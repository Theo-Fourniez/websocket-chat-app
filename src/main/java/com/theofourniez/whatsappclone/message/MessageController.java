package com.theofourniez.whatsappclone.message;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/messages")
class MessageController {

    private final MessageService messageService;
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public ResponseEntity<Message> post(@RequestBody Message message) {
        Message insertedMessage = messageService.save(message);
        return new ResponseEntity<>(insertedMessage, HttpStatus.CREATED);
    }


    @GetMapping
    public ResponseEntity<Iterable<Message>> getAllMessages() {
        return new ResponseEntity<>(messageService.list(), HttpStatus.OK);
    }
}