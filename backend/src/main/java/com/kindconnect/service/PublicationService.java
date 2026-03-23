package com.kindconnect.service;

import com.kindconnect.model.Publication;
import com.kindconnect.model.UserAuthenticated;
import com.kindconnect.repository.PublicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PublicationService {

    private final PublicationRepository publicationRepository;

    @Autowired
    public PublicationService(PublicationRepository publicationRepository) {
        this.publicationRepository = publicationRepository;
    }

    public List<Publication> getAllPublications() {
        return publicationRepository.findAll();
    }

    public List<Publication> getPublicationsByUser(UserAuthenticated user) {
        return publicationRepository.findByUser(user);
    }

    public Page<Publication> getPublicationsByCategorieCode(String code, Pageable pageable) {
        return publicationRepository.findByCategorie_Code(code, pageable);
    }

    public Page<Publication> searchPublications(String q, Pageable pageable) {
        return publicationRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(q, q, pageable);
    }

    public List<Publication> getPublicationsByStatusCode(String code) {
        return publicationRepository.findByStatus_Code(code);
    }

    public List<Publication> getPublicationsByTypeCode(String code) {
        return publicationRepository.findByType_Code(code);
    }
    
    public List<Publication> getAvailablePublications() {
        return publicationRepository.findByEstDisponible(true);
    }
    
    public long countPublicationsByUser(UserAuthenticated user) {
        return publicationRepository.countByUser(user);
    }

    public Optional<Publication> getPublicationById(Long id) {
        return publicationRepository.findById(id);
    }

    @Transactional
    public Publication createPublication(Publication publication) {
        return publicationRepository.save(publication);
    }

    @Transactional
    public Publication updatePublication(Publication publication) {
        return publicationRepository.save(publication);
    }

    @Transactional
    public void deletePublication(Long id) {
        publicationRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return publicationRepository.existsById(id);
    }
}
