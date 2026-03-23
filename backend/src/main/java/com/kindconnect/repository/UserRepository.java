package com.kindconnect.repository;

import com.kindconnect.model.UserAuthenticated;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserAuthenticated, Long> {
    Optional<UserAuthenticated> findByEmail(String email);
    // Fetch roles eagerly for Spring Security authentication flow
    @org.springframework.data.jpa.repository.Query("select u from UserAuthenticated u left join fetch u.roles where u.email = :email")
    Optional<UserAuthenticated> findByEmailFetchRoles(@org.springframework.data.repository.query.Param("email") String email);
    Optional<UserAuthenticated> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
