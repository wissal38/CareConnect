package com.kindconnect.repository;

import com.kindconnect.model.Notification;
import com.kindconnect.model.UserAuthenticated;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientAndIsReadFalse(UserAuthenticated recipient);
    Page<Notification> findByRecipientOrderByCreatedAtDesc(UserAuthenticated recipient, Pageable pageable);
    long countByRecipientAndIsReadFalse(UserAuthenticated recipient);
    List<Notification> findByRecipientAndNotificationType(UserAuthenticated recipient, String notificationType);
}
