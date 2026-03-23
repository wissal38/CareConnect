package com.kindconnect.controller;

import com.kindconnect.dto.MessageRequest;
import com.kindconnect.model.Message;
import com.kindconnect.model.Notification;
import com.kindconnect.model.UserAuthenticated;
import com.kindconnect.repository.NotificationRepository;
import com.kindconnect.repository.PublicationRepository;
import com.kindconnect.repository.UserRepository;
import com.kindconnect.service.MessageService;
import com.kindconnect.service.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private MessageService messageService;

    @Autowired
    private NotificationRepository notificationRepository;

    @PostMapping
    public ResponseEntity<?> sendMessage(@RequestBody MessageRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof UserDetailsImpl)) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        Optional<UserAuthenticated> optSender = userRepository.findById(userDetails.getId());
        if (optSender.isEmpty()) return ResponseEntity.status(401).body("Sender not found");
        Optional<UserAuthenticated> optRecipient = userRepository.findById(request.getRecipientId());
        if (optRecipient.isEmpty()) return ResponseEntity.badRequest().body("Recipient not found");

        Message m = new Message();
        m.setSender(optSender.get());
        m.setRecipient(optRecipient.get());
        m.setContent(request.getContent());
        if (request.getPublicationId() != null) {
            publicationRepository.findById(request.getPublicationId()).ifPresent(m::setPublication);
        }

        Message saved = messageService.save(m);

        // Create a notification for recipient
        Notification n = new Notification();
        n.setRecipient(optRecipient.get());
        n.setTitle("Nouveau message");
        n.setMessage("Vous avez reçu un nouveau message de " + optSender.get().getFirstName());
        n.setNotificationType("MESSAGE");
        n.setReferenceId(saved.getId());
        notificationRepository.save(n);

        return ResponseEntity.ok(saved);
    }

    @GetMapping("/inbox")
    public ResponseEntity<Page<Message>> inbox(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof UserDetailsImpl)) {
            return ResponseEntity.status(401).build();
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        Optional<UserAuthenticated> optUser = userRepository.findById(userDetails.getId());
        if (optUser.isEmpty()) return ResponseEntity.status(401).build();

        Page<Message> messages = messageService.findByRecipient(optUser.get(), PageRequest.of(page, size));
        return ResponseEntity.ok(messages);
    }
}
