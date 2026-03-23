package com.kindconnect.repository;

import com.kindconnect.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StatusRepository extends JpaRepository<Status, Long> {
    Optional<Status> findByCode(String code);
    boolean existsByCode(String code);
    List<Status> findByLibelleContainingIgnoreCase(String libelle);
}
