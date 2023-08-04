package com.theofourniez.whatsappclone.user;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatUserDetailsService implements UserDetailsService {
    private final ChatUserRepository chatUserRepository;

    public ChatUserDetailsService(ChatUserRepository chatUserRepository) {
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


    public void delete(long id) {
        chatUserRepository.deleteById(id);
    }

    @Override
    public ChatUser loadUserByUsername(String username) throws UsernameNotFoundException {
        return chatUserRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(
                "The user with the username " + username + " could not be found !"));
    }

    public ChatUser loadUserWithFriendsByUsername(String username) throws UsernameNotFoundException {
        return chatUserRepository.findWithFriendsByUsername(username).orElseThrow(() -> new UsernameNotFoundException("The user with the username " + username + " could not be found !"));
    }
}
