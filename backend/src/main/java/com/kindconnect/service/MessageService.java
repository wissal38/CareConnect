package com.kindconnect.service;

import com.kindconnect.model.Message;
import com.kindconnect.model.UserAuthenticated;
import com.kindconnect.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Message save(Message message) {
        return messageRepository.save(message);
    }

    public Optional<Message> findById(Long id) {
        return messageRepository.findById(id);
    }

    public List<Message> findBySenderAndRecipient(UserAuthenticated sender, UserAuthenticated recipient) {
        return messageRepository.findBySenderAndRecipientOrderBySentAtDesc(sender, recipient);
    }

    public Page<Message> findByRecipient(UserAuthenticated recipient, Pageable pageable) {
        return messageRepository.findByRecipient(recipient, pageable);
    }

    public Page<Message> findBySender(UserAuthenticated sender, Pageable pageable) {
        return messageRepository.findBySender(sender, pageable);
    }
}
