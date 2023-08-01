package com.theofourniez.whatsappclone.user;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatUserRepository extends JpaRepository<ChatUser, Long> {
    Optional<ChatUser> findByUsername(String username);

    @EntityGraph(attributePaths = "friends")
    Optional<ChatUser> findWithFriendsByUsername(String username);
}
