package com.theofourniez.whatsappclone.message;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/message")
class MessageController {

    private final MessageService messageService;
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public ResponseEntity<Mono<Message>> post(@RequestBody Message message) {
        Message insertedMessage = messageService.save(message);
        return new ResponseEntity<>(Mono.just(insertedMessage), HttpStatus.CREATED);
    }


    @GetMapping
    public ResponseEntity<Mono<Iterable<Message>>> getAllMessages() {
        return new ResponseEntity<>(Mono.just(messageService.list()), HttpStatus.OK);
    }
}