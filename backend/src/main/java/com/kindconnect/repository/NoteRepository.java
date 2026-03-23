package com.kindconnect.repository;

import com.kindconnect.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    Optional<Note> findByCode(String code);
    boolean existsByCode(String code);
    List<Note> findByLibelleContainingIgnoreCase(String libelle);
    List<Note> findByValeurGreaterThanEqual(int valeur);
}
