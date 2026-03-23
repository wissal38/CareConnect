package com.kindconnect.config;

import com.kindconnect.security.jwt.AuthTokenFilter;
import com.kindconnect.security.jwt.JwtAuthEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    private JwtAuthEntryPoint unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> 
                auth
                    // Pages accessibles sans authentification
                    .requestMatchers("/", "/index", "/home", "/error").permitAll()
                    .requestMatchers("/connexion", "/connexion/**", "/connexion.html").permitAll()
                    .requestMatchers("/demandes", "/demandes/**").permitAll()
                    
                    // Ressources statiques
                    .requestMatchers(
                        "/css/**", 
                        "/js/**", 
                        "/images/**", 
                        "/webjars/**", 
                        "/static/**", 
                        "/assets/**",
                        "/favicon.ico"
                    ).permitAll()
                    
                    // API et documentation
                    .requestMatchers("/api/auth/**").permitAll()
                    // Allow public read of publications but require auth for creating/updating/deleting
                    .requestMatchers(HttpMethod.GET, "/api/publications/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/publications/**").authenticated()
                    .requestMatchers(HttpMethod.PUT, "/api/publications/**").authenticated()
                    .requestMatchers(HttpMethod.DELETE, "/api/publications/**").authenticated()
                    .requestMatchers("/v2/api-docs").permitAll()
                    .requestMatchers("/v3/api-docs").permitAll()
                    .requestMatchers("/v3/api-docs/**").permitAll()
                    .requestMatchers("/swagger-ui/**").permitAll()
                    .requestMatchers("/swagger-resources/**").permitAll()
                    .requestMatchers("/configuration/**").permitAll()
                    .requestMatchers("/swagger-ui.html").permitAll()
                    .requestMatchers("/api/test/**").permitAll()
                    
                    // Autoriser l'accès aux fichiers statiques (already covered by folder patterns above)
                    // (Old extension-based path patterns sometimes cause PathPattern parsing errors)
                    
                    // Admin area: only ROLE_ADMIN
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    // Toutes les autres requêtes nécessitent une authentification
                    .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/connexion")
                .permitAll()
            )
            .logout(logout -> logout
                .permitAll()
            );

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // For security and cookie handling in development, list allowed origins explicitly
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:8081", "http://localhost:8082"));
        // Allow cookies (necessary if clients rely on HttpOnly cookie for JWT)
        configuration.setAllowCredentials(true);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
        configuration.setExposedHeaders(Arrays.asList("x-auth-token"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
