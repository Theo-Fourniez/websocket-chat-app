package com.theofourniez.whatsappclone.message;

import com.theofourniez.whatsappclone.user.ChatUser;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository){
        this.messageRepository = messageRepository;
    }
    public List<Message> list() {
        return messageRepository.findAll();
    }

    public Message save(Message message) {
        return messageRepository.save(message);
    }

    public Optional<Message> get(long id) {
        return messageRepository.findById(id);
    }

    public void delete(long id) {
        messageRepository.deleteById(id);
    }

    public List<Message> getSentMessages(ChatUser sender) {
        return messageRepository.findAllBySender(sender);
    }

    public List<Message> getConversationBetween(ChatUser sender, ChatUser recipient) {
        return messageRepository.findAllMessagesBetweenSenderAndRecipient(sender, recipient);
    }
}