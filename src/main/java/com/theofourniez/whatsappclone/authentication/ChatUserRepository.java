package com.theofourniez.whatsappclone.authentication;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatUserRepository extends JpaRepository<ChatUser, Long> {
    Optional<ChatUser> findByUsername(String username);
}
