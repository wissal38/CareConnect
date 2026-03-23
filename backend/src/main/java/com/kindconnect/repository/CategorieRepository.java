package com.kindconnect.repository;

import com.kindconnect.model.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategorieRepository extends JpaRepository<Categorie, Long> {
    Optional<Categorie> findByCode(String code);
    boolean existsByCode(String code);
    List<Categorie> findByLabelContainingIgnoreCase(String label);
    long count();
}
