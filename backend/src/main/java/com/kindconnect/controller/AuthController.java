package com.kindconnect.controller;

import com.kindconnect.dto.JwtResponse;
import com.kindconnect.dto.LoginRequest;
import com.kindconnect.dto.SignupRequest;
import com.kindconnect.model.ERole;
import com.kindconnect.model.Role;
import com.kindconnect.model.UserAuthenticated;
import com.kindconnect.repository.RoleRepository;
import com.kindconnect.repository.UserRepository;
import com.kindconnect.security.jwt.JwtUtils;
import com.kindconnect.service.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    com.kindconnect.repository.BlockedEmailRepository blockedEmailRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();        
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // Try to include first/last name if available
        var optUser = userRepository.findById(userDetails.getId());
        String firstName = null, lastName = null;
        if (optUser.isPresent()) {
            firstName = optUser.get().getFirstName();
            lastName = optUser.get().getLastName();
        }
        JwtResponse jwtResponse = new JwtResponse(
            jwt,
            userDetails.getId(),
            userDetails.getUsername(),
            userDetails.getEmail(),
            firstName,
            lastName,
            roles);

        // Create HttpOnly cookie so server will receive the token on next page requests
        var cookie = org.springframework.http.ResponseCookie.from("ACCESS_TOKEN", jwt)
            .httpOnly(true)
            .path("/")
            .maxAge(jwtUtils.getJwtExpirationSeconds())
            .sameSite("Lax")
            .secure(false) // set to true in production (HTTPS)
            .build();

        return ResponseEntity.ok().header("Set-Cookie", cookie.toString()).body(jwtResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Email is already in use!");
        }

        if (blockedEmailRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                .badRequest()
                .body("Error: This email has been blocked by an administrator.");
        }

        // Create new user's account
        UserAuthenticated user = new UserAuthenticated();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());

        Set<String> strRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity
                .ok()
                .body(Map.of("message", "User registered successfully!"));
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logout() {
        var cookie = org.springframework.http.ResponseCookie.from("ACCESS_TOKEN", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .secure(false)
                .build();
        return ResponseEntity.ok().header("Set-Cookie", cookie.toString()).body(Map.of("message", "Signed out"));
    }
}
