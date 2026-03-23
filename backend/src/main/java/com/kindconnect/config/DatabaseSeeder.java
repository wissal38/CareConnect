package com.kindconnect.config;

import com.kindconnect.model.Publication;
import com.kindconnect.model.Role;
import com.kindconnect.model.UserAuthenticated;
import com.kindconnect.model.ERole;
import com.kindconnect.repository.PublicationRepository;
import com.kindconnect.repository.RoleRepository;
import com.kindconnect.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@Order(1)
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create default roles if missing
        if (!roleRepository.findByName(ERole.ROLE_USER).isPresent()) {
            Role r = new Role();
            r.setName(ERole.ROLE_USER);
            roleRepository.save(r);
        }
        if (!roleRepository.findByName(ERole.ROLE_ADMIN).isPresent()) {
            Role r = new Role();
            r.setName(ERole.ROLE_ADMIN);
            roleRepository.save(r);
        }

        // Create demo user if doesn't exist
        String demoEmail = "demo@careconnect.tn";
        UserAuthenticated demo = userRepository.findByEmail(demoEmail).orElse(null);
        if (demo == null) {
            demo = new UserAuthenticated();
            demo.setUsername("demo");
            demo.setEmail(demoEmail);
            demo.setFirstName("Jean");
            demo.setLastName("Dupont");
            demo.setPassword(passwordEncoder.encode("password"));
            demo.setCity("Tunis");
            Set<Role> roles = new HashSet<>();
            roleRepository.findByName(ERole.ROLE_USER).ifPresent(roles::add);
            demo.setRoles(roles);
            userRepository.save(demo);
        }

        // Create admin user if doesn't exist
        String adminEmail = "admin@careconnect.tn";
        UserAuthenticated admin = userRepository.findByEmail(adminEmail).orElse(null);
        if (admin == null) {
            admin = new UserAuthenticated();
            admin.setUsername("admin");
            admin.setEmail(adminEmail);
            admin.setFirstName("Admin");
            admin.setLastName("Account");
            // Default password - please change in production
            admin.setPassword(passwordEncoder.encode("Admin123!"));
            admin.setCity("Tunis");
            Set<Role> adminRoles = new HashSet<>();
            roleRepository.findByName(ERole.ROLE_USER).ifPresent(adminRoles::add);
            roleRepository.findByName(ERole.ROLE_ADMIN).ifPresent(adminRoles::add);
            admin.setRoles(adminRoles);
            userRepository.save(admin);
        }

        // Create sample publications if none
        if (publicationRepository.count() == 0) {
            Publication p1 = new Publication();
            p1.setTitle("Aide pour courses hebdomadaires");
            p1.setDescription("Je peux faire les courses pour les personnes âgées.");
            p1.setLocation("Tunis, 1001");
            p1.setEstDisponible(true);
            p1.setUser(demo);
            publicationRepository.save(p1);

            Publication p2 = new Publication();
            p2.setTitle("Accompagnement rendez-vous médical");
            p2.setDescription("Accompagnement pour rendez-vous à l'hôpital.");
            p2.setLocation("Sfax, 3000");
            p2.setEstDisponible(true);
            p2.setUser(demo);
            publicationRepository.save(p2);

            Publication p3 = new Publication();
            p3.setTitle("Aide informatique - installation logiciel");
            p3.setDescription("Aide pour installer des logiciels et résoudre les problèmes basiques.");
            p3.setLocation("Sousse, 4000");
            p3.setEstDisponible(true);
            p3.setUser(demo);
            publicationRepository.save(p3);
        }
    }
}
