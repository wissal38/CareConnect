package com.kindconnect.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Getter
@Setter
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private UserAuthenticated sender;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private UserAuthenticated recipient;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publication_id")
    private Publication publication;
    
    @Column(nullable = false)
    private LocalDateTime sentAt = LocalDateTime.now();
    
    private boolean isRead = false;

    // Explicit accessors to ensure compilation if Lombok processing is unavailable
    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }
    public String getContent() { return this.content; }
    public void setContent(String content) { this.content = content; }
    public UserAuthenticated getSender() { return this.sender; }
    public void setSender(UserAuthenticated sender) { this.sender = sender; }
    public UserAuthenticated getRecipient() { return this.recipient; }
    public void setRecipient(UserAuthenticated recipient) { this.recipient = recipient; }
    public Publication getPublication() { return this.publication; }
    public void setPublication(Publication publication) { this.publication = publication; }
    public LocalDateTime getSentAt() { return this.sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
    public boolean isRead() { return this.isRead; }
    public void setRead(boolean read) { this.isRead = read; }
}
