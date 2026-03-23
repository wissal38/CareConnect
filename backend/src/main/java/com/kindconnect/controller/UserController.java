package com.kindconnect.controller;

import com.kindconnect.model.UserAuthenticated;
import com.kindconnect.repository.UserRepository;
import com.kindconnect.service.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import com.kindconnect.service.UserService;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof UserDetailsImpl))
            return ResponseEntity.status(401).build();
        UserDetailsImpl ud = (UserDetailsImpl) auth.getPrincipal();
        Optional<UserAuthenticated> u = userRepository.findById(ud.getId());
        return u.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(404).build());
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, String> body) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return ResponseEntity.status(401).build();
        UserDetailsImpl ud = (UserDetailsImpl) auth.getPrincipal();
        Optional<UserAuthenticated> opt = userRepository.findById(ud.getId());
        if (opt.isEmpty()) return ResponseEntity.status(404).build();
        UserAuthenticated u = opt.get();

        if (body.containsKey("firstName")) u.setFirstName(body.get("firstName"));
        if (body.containsKey("lastName")) u.setLastName(body.get("lastName"));
        if (body.containsKey("phoneNumber")) u.setPhoneNumber(body.get("phoneNumber"));
        if (body.containsKey("city")) u.setCity(body.get("city"));
        if (body.containsKey("profilePicture")) u.setProfilePicture(body.get("profilePicture"));

        userRepository.save(u);
        return ResponseEntity.ok(u);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) return ResponseEntity.notFound().build();
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/block")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> blockUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) return ResponseEntity.notFound().build();
        userService.blockUser(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/unblock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> unblockUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) return ResponseEntity.notFound().build();
        userService.unblockUser(id);
        return ResponseEntity.ok().build();
    }

    // Duplicate endpoints removed – the block/unblock actions are handled by the previous methods
}
