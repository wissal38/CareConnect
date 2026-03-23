package com.kindconnect.repository;

import com.kindconnect.model.BlockedEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlockedEmailRepository extends JpaRepository<BlockedEmail, Long> {
    boolean existsByEmail(String email);
    Optional<BlockedEmail> findByEmail(String email);
}
