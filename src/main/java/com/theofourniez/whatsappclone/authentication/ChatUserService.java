package com.theofourniez.whatsappclone.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ChatUserService implements UserDetailsService {

    @Autowired
    private ChatUserRepository chatUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return chatUserRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("Username " + username + " not found"));
    }
}
