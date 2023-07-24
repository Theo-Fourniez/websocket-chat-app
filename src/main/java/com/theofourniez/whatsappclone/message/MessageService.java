package com.theofourniez.whatsappclone.message;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    @Autowired
    private MessageRepository bookRepository;

    public List<Message> list() {
        return bookRepository.findAll();
    }

    public Message save(Message message) {
        return bookRepository.save(message);
    }

    public Optional<Message> get(long id) {
        return bookRepository.findById(id);
    }

    public void delete(long id) {
        bookRepository.deleteById(id);
    }
}