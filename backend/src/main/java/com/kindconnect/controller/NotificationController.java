package com.kindconnect.controller;

import com.kindconnect.model.Notification;
import com.kindconnect.model.UserAuthenticated;
import com.kindconnect.repository.NotificationRepository;
import com.kindconnect.repository.UserRepository;
import com.kindconnect.service.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    // Get all unread notifications for current user
    @GetMapping("/unread")
    public java.util.List<Notification> getUnreadNotifications() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof UserDetailsImpl)) {
            return List.of();
        }
        UserDetailsImpl ud = (UserDetailsImpl) auth.getPrincipal();
        var optUser = userRepository.findById(ud.getId());
        if (optUser.isEmpty()) return List.of();
        return notificationRepository.findByRecipientAndIsReadFalse(optUser.get());
    }

    // Get all notifications for current user
    @GetMapping
    public java.util.List<Notification> getAllNotifications() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof UserDetailsImpl)) {
            return List.of();
        }
        UserDetailsImpl ud = (UserDetailsImpl) auth.getPrincipal();
        var optUser = userRepository.findById(ud.getId());
        if (optUser.isEmpty()) return List.of();
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(optUser.get(), org.springframework.data.domain.PageRequest.of(0, 50)).getContent();
    }

    // Mark notification as read
    @PostMapping("/{id}/read")
    public java.util.Map<String, String> markAsRead(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof UserDetailsImpl)) {
            return Map.of("error", "Not authenticated");
        }
        var optNotif = notificationRepository.findById(id);
        if (optNotif.isEmpty()) {
            return Map.of("error", "Notification not found");
        }
        Notification notif = optNotif.get();
        UserDetailsImpl ud = (UserDetailsImpl) auth.getPrincipal();
        if (!notif.getRecipient().getId().equals(ud.getId())) {
            return Map.of("error", "Unauthorized");
        }
        notif.setRead(true);
        notificationRepository.save(notif);
        return Map.of("status", "success");
    }

    // Get unread count
    @GetMapping("/unread/count")
    public java.util.Map<String, Long> getUnreadCount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof UserDetailsImpl)) {
            return Map.of("count", 0L);
        }
        UserDetailsImpl ud = (UserDetailsImpl) auth.getPrincipal();
        var optUser = userRepository.findById(ud.getId());
        if (optUser.isEmpty()) return Map.of("count", 0L);
        long count = notificationRepository.countByRecipientAndIsReadFalse(optUser.get());
        return Map.of("count", count);
    }

    // Get notifications by type
    @GetMapping("/type/{type}")
    public java.util.List<Notification> getNotificationsByType(@PathVariable String type) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof UserDetailsImpl)) {
            return List.of();
        }
        UserDetailsImpl ud = (UserDetailsImpl) auth.getPrincipal();
        var optUser = userRepository.findById(ud.getId());
        if (optUser.isEmpty()) return List.of();
        return notificationRepository.findByRecipientAndNotificationType(optUser.get(), type);
    }
}
