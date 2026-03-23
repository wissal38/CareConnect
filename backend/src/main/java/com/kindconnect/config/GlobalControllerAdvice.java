package com.kindconnect.config;

import com.kindconnect.model.UserAuthenticated;
import com.kindconnect.repository.UserRepository;
import com.kindconnect.service.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

import java.util.Optional;

@ControllerAdvice(annotations = Controller.class)
public class GlobalControllerAdvice {

    @Autowired
    private UserRepository userRepository;

    @ModelAttribute
    public void addUserToModel(Model model) {
        // Default isAdmin to false so SpEL checks don't fail when value is not present
        model.addAttribute("isAdmin", false);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl ud = (UserDetailsImpl) auth.getPrincipal();
            Optional<UserAuthenticated> u = userRepository.findById(ud.getId());
            u.ifPresent(user -> {
                model.addAttribute("user", user);
                String fullName = ((user.getFirstName() != null ? user.getFirstName() : "") + " " + (user.getLastName() != null ? user.getLastName() : "")).trim();
                if (fullName.isEmpty()) fullName = user.getUsername();
                model.addAttribute("userFullName", fullName);
                // Expose a simple boolean to templates for admin-only display
                boolean isAdmin = user.getRoles().stream().anyMatch(r -> r.getName() != null && r.getName().name().equals("ROLE_ADMIN"));
                model.addAttribute("isAdmin", isAdmin);
            });
        }
    }
}
