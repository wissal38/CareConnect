package com.kindconnect.controller;

import com.kindconnect.model.Publication;
import com.kindconnect.model.UserAuthenticated;
import com.kindconnect.repository.UserRepository;
import com.kindconnect.repository.NotificationRepository;
import com.kindconnect.model.Notification;
import com.kindconnect.service.PublicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.kindconnect.service.UserDetailsImpl;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/publications")
public class PublicationController {

    private final PublicationService publicationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    public PublicationController(PublicationService publicationService) {
        this.publicationService = publicationService;
    }

    @GetMapping
    public ResponseEntity<List<Publication>> getAllPublications() {
        return ResponseEntity.ok(publicationService.getAllPublications());
    }

    @GetMapping("/available")
    public ResponseEntity<List<Publication>> getAvailablePublications() {
        return ResponseEntity.ok(publicationService.getAvailablePublications());
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Publication>> search(@RequestParam String q, Pageable pageable) {
        return ResponseEntity.ok(publicationService.searchPublications(q, pageable));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Publication>> getPublicationsByUser(@PathVariable Long userId) {
        // Créer un objet UserAuthenticated temporaire avec l'ID fourni
        // Note: Dans une application réelle, vous récupéreriez l'utilisateur à partir du contexte de sécurité
        UserAuthenticated user = new UserAuthenticated();
        user.setId(userId);
        return ResponseEntity.ok(publicationService.getPublicationsByUser(user));
    }

    @GetMapping("/categorie/{code}")
    public ResponseEntity<Page<Publication>> getPublicationsByCategorie(
            @PathVariable String code, 
            Pageable pageable) {
        return ResponseEntity.ok(publicationService.getPublicationsByCategorieCode(code, pageable));
    }

    @GetMapping("/me")
    public ResponseEntity<List<Publication>> getMyPublications() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof UserDetailsImpl)) {
            return ResponseEntity.status(401).build();
        }
        UserDetailsImpl ud = (UserDetailsImpl) auth.getPrincipal();
        var optUser = userRepository.findById(ud.getId());
        if (optUser.isEmpty()) return ResponseEntity.status(404).build();
        return ResponseEntity.ok(publicationService.getPublicationsByUser(optUser.get()));
    }

    @GetMapping("/status/{code}")
    public ResponseEntity<List<Publication>> getPublicationsByStatus(@PathVariable String code) {
        return ResponseEntity.ok(publicationService.getPublicationsByStatusCode(code));
    }

    @GetMapping("/type/{code}")
    public ResponseEntity<List<Publication>> getPublicationsByType(@PathVariable String code) {
        return ResponseEntity.ok(publicationService.getPublicationsByTypeCode(code));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Publication> getPublicationById(@PathVariable Long id) {
        return publicationService.getPublicationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Publication> createPublication(@RequestBody Publication publication) {
        // Ensure the authenticated user is assigned as owner
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof UserDetailsImpl)) {
            return ResponseEntity.status(401).build();
        }
        UserDetailsImpl ud = (UserDetailsImpl) auth.getPrincipal();
        // Load the actual user from DB to avoid detached entity issues
        var optUser = userRepository.findById(ud.getId());
        optUser.ifPresent(publication::setUser);
        var saved = publicationService.createPublication(publication);
        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Publication> updatePublication(
            @PathVariable Long id, 
            @RequestBody Publication publication) {
        if (!publicationService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        // Check that the current user is the owner
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof UserDetailsImpl)) {
            return ResponseEntity.status(401).build();
        }
        UserDetailsImpl ud = (UserDetailsImpl) auth.getPrincipal();
        var existingOpt = publicationService.getPublicationById(id);
        if (existingOpt.isEmpty()) return ResponseEntity.notFound().build();
        var existing = existingOpt.get();
        boolean isOwner = existing.getUser() != null && existing.getUser().getId().equals(ud.getId());
        boolean isAdmin = ud.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isOwner && !isAdmin) return ResponseEntity.status(403).build();
        publication.setId(id);
        userRepository.findById(ud.getId()).ifPresent(publication::setUser);
        return ResponseEntity.ok(publicationService.updatePublication(publication));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePublication(@PathVariable Long id) {
        if (!publicationService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        // Only owner or admin can delete: simplified for now (owner check)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof UserDetailsImpl)) return ResponseEntity.status(401).build();
        UserDetailsImpl ud = (UserDetailsImpl) auth.getPrincipal();
        var existingOpt = publicationService.getPublicationById(id);
        if (existingOpt.isEmpty()) return ResponseEntity.notFound().build();
        var existing = existingOpt.get();
        boolean isOwner = existing.getUser() != null && existing.getUser().getId().equals(ud.getId());
        boolean isAdmin = ud.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isOwner && !isAdmin) return ResponseEntity.status(403).build();
        // TODO: enforce owner/admin check
        publicationService.deletePublication(id);
        return ResponseEntity.noContent().build();
    }

    // Mark publication as completed (set estDisponible=false)
    @PostMapping("/{id}/complete")
    public ResponseEntity<Publication> completePublication(@PathVariable Long id) {
        var opt = publicationService.getPublicationById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        var pub = opt.get();
        pub.setEstDisponible(false);
        // Notify owner the publication has been marked completed (optional)
        if (pub.getUser() != null) {
            Notification n = new Notification();
            n.setRecipient(pub.getUser());
            n.setTitle("Publication terminée");
            n.setMessage("Votre publication '" + pub.getTitle() + "' a été marquée comme terminée.");
            n.setNotificationType("PUBLICATION_COMPLETED");
            n.setReferenceId(pub.getId());
            notificationRepository.save(n);
        }
        return ResponseEntity.ok(publicationService.updatePublication(pub));
    }

    // Propose help for a publication
    @PostMapping("/{id}/propose-help")
    public ResponseEntity<?> proposeHelp(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof UserDetailsImpl)) {
            return ResponseEntity.status(401).build();
        }

        var optPub = publicationService.getPublicationById(id);
        if (optPub.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Publication pub = optPub.get();
        UserDetailsImpl ud = (UserDetailsImpl) auth.getPrincipal();
        var optHelper = userRepository.findById(ud.getId());

        if (optHelper.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        UserAuthenticated helper = optHelper.get();

        // Avoid self-proposals
        if (pub.getUser() != null && pub.getUser().getId().equals(helper.getId())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Vous ne pouvez pas proposer votre aide sur votre propre publication"));
        }

        // Send notification to publication owner
        if (pub.getUser() != null) {
            Notification notification = new Notification();
            notification.setRecipient(pub.getUser());
            notification.setTitle("Nouvelle proposition d'aide");
            notification.setMessage(helper.getFirstName() + " " + helper.getLastName() + " a proposé son aide pour \"" + pub.getTitle() + "\"");
            notification.setNotificationType("HELP_PROPOSED");
            notification.setReferenceId(pub.getId());
            notificationRepository.save(notification);
        }

        return ResponseEntity.ok(Map.of("message", "Votre proposition d'aide a été envoyée avec succès"));
    }

    // Thank the owner of a publication
    @PostMapping("/{id}/thank")
    public ResponseEntity<?> thankOwner(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof UserDetailsImpl)) return ResponseEntity.status(401).build();
        var opt = publicationService.getPublicationById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        var pub = opt.get();
        if (pub.getUser() == null) return ResponseEntity.badRequest().body("Publication has no owner");

        var ud = (UserDetailsImpl) auth.getPrincipal();
        userRepository.findById(ud.getId()).ifPresent(sender -> {
            Notification n = new Notification();
            n.setRecipient(pub.getUser());
            n.setTitle("Merci reçu");
            n.setMessage(sender.getFirstName() + " a remercié votre publication: " + pub.getTitle());
            n.setNotificationType("THANK");
            n.setReferenceId(pub.getId());
            notificationRepository.save(n);
        });

        return ResponseEntity.ok(Map.of("message", "Merci envoyé"));
    }
}
