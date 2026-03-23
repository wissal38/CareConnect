package com.kindconnect.controller;

import com.kindconnect.repository.UserRepository;
import com.kindconnect.service.PublicationService;
import com.kindconnect.model.UserAuthenticated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PublicationService publicationService;

    @GetMapping("/admin")
    public String adminHome(Model model) {
        model.addAttribute("usersCount", userRepository.count());
        model.addAttribute("publicationsCount", publicationService.getAllPublications().size());
        return "admin";
    }

    @GetMapping("/admin/users")
    public String adminUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin_users";
    }

    @GetMapping("/admin/publications")
    public String adminPublications(Model model) {
        model.addAttribute("publications", publicationService.getAllPublications());
        return "admin_publications";
    }
}
