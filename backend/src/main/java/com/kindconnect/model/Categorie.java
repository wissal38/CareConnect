package com.kindconnect.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categorie")
@Getter
@Setter
public class Categorie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String code;
    
    @Column(nullable = false)
    private String label;
    
    @OneToMany(mappedBy = "categorie")
    private Set<Publication> publications = new HashSet<>();

    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }
}
