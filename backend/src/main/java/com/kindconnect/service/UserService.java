package com.kindconnect.service;

import com.kindconnect.model.UserAuthenticated;
import com.kindconnect.repository.UserRepository;
import com.kindconnect.repository.BlockedEmailRepository;
import com.kindconnect.model.BlockedEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BlockedEmailRepository blockedEmailRepository;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, BlockedEmailRepository blockedEmailRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.blockedEmailRepository = blockedEmailRepository;
    }

    public List<UserAuthenticated> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<UserAuthenticated> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<UserAuthenticated> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Transactional
    public UserAuthenticated createUser(UserAuthenticated user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Transactional
    public UserAuthenticated updateUser(UserAuthenticated user) {
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public void blockUser(Long id) {
        Optional<UserAuthenticated> opt = userRepository.findById(id);
        if (opt.isPresent()) {
            UserAuthenticated u = opt.get();
            u.setActive(false);
            userRepository.save(u);
            try {
                if (!blockedEmailRepository.existsByEmail(u.getEmail())) {
                    BlockedEmail be = new BlockedEmail();
                    be.setEmail(u.getEmail());
                    blockedEmailRepository.save(be);
                }
            } catch (Exception e) {
                // don't fail overall operation if blocked email can't be saved
            }
        }
    }

    @Transactional
    public void unblockUser(Long id) {
        Optional<UserAuthenticated> opt = userRepository.findById(id);
        if (opt.isPresent()) {
            UserAuthenticated u = opt.get();
            u.setActive(true);
            userRepository.save(u);
            try {
                var optBe = blockedEmailRepository.findByEmail(u.getEmail());
                optBe.ifPresent(be -> blockedEmailRepository.delete(be));
            } catch (Exception e) {
                // ignore cleanup errors
            }
        }
    }
}
