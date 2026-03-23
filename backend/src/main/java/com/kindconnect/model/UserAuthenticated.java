package com.kindconnect.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user_authenticated")
@Getter
@Setter
@JsonIgnoreProperties({"publications", "receivedMessages", "sentMessages", "reviews", "notifications"})
public class UserAuthenticated extends User {
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Publication> publications = new HashSet<>();
    
    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL)
    private Set<Message> receivedMessages = new HashSet<>();
    
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    private Set<Message> sentMessages = new HashSet<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Review> reviews = new HashSet<>();
    
    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL)
    private Set<Notification> notifications = new HashSet<>();
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public Set<Role> getRoles() { return this.roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }
}
