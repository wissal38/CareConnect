package com.kindconnect.repository;

import com.kindconnect.model.Message;
import com.kindconnect.model.UserAuthenticated;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderAndRecipientOrderBySentAtDesc(UserAuthenticated sender, UserAuthenticated recipient);
    List<Message> findByRecipientAndIsReadFalse(UserAuthenticated recipient);
    long countByRecipientAndIsReadFalse(UserAuthenticated recipient);
    Page<Message> findByRecipient(UserAuthenticated recipient, Pageable pageable);
    Page<Message> findBySender(UserAuthenticated sender, Pageable pageable);
}
