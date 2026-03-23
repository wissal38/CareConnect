package com.kindconnect.repository;

import com.kindconnect.model.Publication;
import com.kindconnect.model.UserAuthenticated;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublicationRepository extends JpaRepository<Publication, Long> {
    List<Publication> findByUser(UserAuthenticated user);
    Page<Publication> findByCategorie_Code(String codeCategorie, Pageable pageable);
    Page<Publication> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description, Pageable pageable);
    List<Publication> findByStatus_Code(String statusCode);
    List<Publication> findByType_Code(String typeCode);
    List<Publication> findByEstDisponible(boolean estDisponible);
    long countByUser(UserAuthenticated user);
}
