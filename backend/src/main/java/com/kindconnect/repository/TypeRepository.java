package com.kindconnect.repository;

import com.kindconnect.model.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TypeRepository extends JpaRepository<Type, Long> {
    Optional<Type> findByCode(String code);
    boolean existsByCode(String code);
    List<Type> findByLibelleContainingIgnoreCase(String libelle);
}
