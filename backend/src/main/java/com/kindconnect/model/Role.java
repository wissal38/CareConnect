package com.kindconnect.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "role")
@Getter
@Setter
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ERole name;
    
    @ManyToMany(mappedBy = "roles")
    @JsonIgnore
    private Set<UserAuthenticated> users = new HashSet<>();

    // explicit mutator for Role name
    public void setName(ERole name) { this.name = name; }
    public ERole getName() { return this.name; }
}
