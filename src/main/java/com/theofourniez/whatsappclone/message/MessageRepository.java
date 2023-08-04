package com.theofourniez.whatsappclone.message;

import com.theofourniez.whatsappclone.user.ChatUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    public List<Message> findAllBySender(ChatUser sender);

    public List<Message> findAllByRecipient(ChatUser recipient);

    @Query("SELECT m FROM Message m WHERE (m.sender = :sender AND m.recipient = :recipient) OR (m.sender = :recipient AND m.recipient = :sender)")
    public List<Message> findAllMessagesBetweenSenderAndRecipient(@Param("sender") ChatUser sender, @Param("recipient") ChatUser recipient);
}
