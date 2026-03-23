package com.kindconnect.service;

import com.kindconnect.model.UserAuthenticated;
import com.kindconnect.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.kindconnect.service.UserDetailsImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

        @Override
        public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Use fetch query to ensure roles collection is initialized (avoid LazyInitializationException)
        UserAuthenticated user = userRepository.findByEmailFetchRoles(email)
            .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé : " + email));

        // Build custom UserDetailsImpl from domain user
        return UserDetailsImpl.build(user);
        }
}
