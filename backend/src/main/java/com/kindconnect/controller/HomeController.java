package com.kindconnect.controller;

import com.kindconnect.service.PublicationService;
import com.kindconnect.service.UserDetailsImpl;
import com.kindconnect.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Optional;
import com.kindconnect.model.UserAuthenticated;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class HomeController {

    @Autowired
    private PublicationService publicationService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String home(Model model) {
        // Provide latest available publications for homepage (limit to 6)
        var list = publicationService.getAvailablePublications();
        if (list != null && list.size() > 6) {
            model.addAttribute("publications", list.subList(0, 6));
        } else {
            model.addAttribute("publications", list);
        }
        // Add authenticated user if present
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl ud = (UserDetailsImpl) auth.getPrincipal();
            Optional<UserAuthenticated> u = userRepository.findById(ud.getId());
            u.ifPresent(user -> model.addAttribute("user", user));
        }
        return "index";
    }
    
    @GetMapping("/connexion")
    public String login(Model model) {
        // If user is already authenticated, provide it to the template so header can render server-side fallback
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl ud = (UserDetailsImpl) auth.getPrincipal();
            Optional<UserAuthenticated> u = userRepository.findById(ud.getId());
            u.ifPresent(user -> model.addAttribute("user", user));
        }
        return "connexion";
    }
    
    @GetMapping("/demandes")
    public String demandes(Model model) {
        model.addAttribute("publications", publicationService.getAvailablePublications());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl ud = (UserDetailsImpl) auth.getPrincipal();
            Optional<UserAuthenticated> u = userRepository.findById(ud.getId());
            u.ifPresent(user -> model.addAttribute("user", user));
        }
        return "listedemandes";
    }
    
    @GetMapping("/compte")
    public String compte(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            Object principal = auth.getPrincipal();
            if (principal instanceof UserDetailsImpl) {
                UserDetailsImpl ud = (UserDetailsImpl) principal;
                Optional<UserAuthenticated> u = userRepository.findById(ud.getId());
                u.ifPresent(user -> {
                    model.addAttribute("user", user);
                    String fullName = ((user.getFirstName() != null ? user.getFirstName() : "") + " " + (user.getLastName() != null ? user.getLastName() : "")).trim();
                    if (fullName.isEmpty()) fullName = user.getUsername();
                    model.addAttribute("userFullName", fullName);
                    // Add the user's own publications to the model for rendering on the profile page
                    try {
                        var pubs = publicationService.getPublicationsByUser(user);
                        model.addAttribute("userPublications", pubs);
                        model.addAttribute("userPublicationsCount", pubs == null ? 0 : pubs.size());
                    } catch (Exception ex) {
                        // gracefully degrade: in case of an error, do not let the profile crash
                        model.addAttribute("userPublications", null);
                        model.addAttribute("userPublicationsCount", 0);
                    }
                });
            }
        }
        return "compte";
    }
}
