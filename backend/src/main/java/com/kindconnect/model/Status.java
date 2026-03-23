package com.kindconnect.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "status")
@Getter
@Setter
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String code;
    
    @Column(nullable = false)
    private String libelle;
    
    @OneToMany(mappedBy = "status")
    private Set<Publication> publications = new HashSet<>();

    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }
}
