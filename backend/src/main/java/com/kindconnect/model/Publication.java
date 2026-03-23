package com.kindconnect.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "publications")
@Getter
@Setter
@JsonIgnoreProperties({"user", "messages", "reviews"})
public class Publication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private String imageUrl;
    private boolean estDisponible = true;
    private String location;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAuthenticated user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categorie_id")
    private Categorie categorie;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private Status status;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    private Type type;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id")
    private Note note;
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime updatedAt;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
    
    @OneToMany(mappedBy = "publication", cascade = CascadeType.ALL)
    private Set<Message> messages = new HashSet<>();
    
    @OneToMany(mappedBy = "publication", cascade = CascadeType.ALL)
    private Set<Review> reviews = new HashSet<>();

    // Explicit accessors for critical fields
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return this.description; }
    public void setDescription(String description) { this.description = description; }
    public String getLocation() { return this.location; }
    public void setLocation(String location) { this.location = location; }
    public boolean getEstDisponible() { return this.estDisponible; }
    public void setEstDisponible(boolean estDisponible) { this.estDisponible = estDisponible; }
    public UserAuthenticated getUser() { return this.user; }
    public void setUser(UserAuthenticated user) { this.user = user; }

    public boolean getIsActive() { return this.isActive; }
    public void setIsActive(boolean active) { this.isActive = active; }
}
