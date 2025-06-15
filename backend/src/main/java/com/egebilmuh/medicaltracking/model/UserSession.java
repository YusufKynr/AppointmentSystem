package com.egebilmuh.medicaltracking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UserSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int sessionId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String sessionToken;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private boolean isActive;
    private String userAgent;
    private String ipAddress;

    public UserSession(User user, String sessionToken, LocalDateTime expiresAt) {
        this.user = user;
        this.sessionToken = sessionToken;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = expiresAt;
        this.isActive = true;
    }
} 