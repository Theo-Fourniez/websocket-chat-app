package com.theofourniez.whatsappclone.authentication;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatUserService {
    private ChatUserRepository chatUserRepository;

    public ChatUserService(ChatUserRepository chatUserRepository) {
        this.chatUserRepository = chatUserRepository;
    }

    public List<ChatUser> list() {
        return chatUserRepository.findAll();
    }

    public ChatUser save(ChatUser chatUser) {
        return chatUserRepository.save(chatUser);
    }

    public Optional<ChatUser> get(long id) {
        return chatUserRepository.findById(id);
    }

    public Optional<ChatUser> findBySpringUsername(String springUsername){return chatUserRepository.findBySpringUsername(springUsername);}

    public void delete(long id) {
        chatUserRepository.deleteById(id);
    }
    }
