package com.krzywdek19.auth_service.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    @OneToOne
    private User user;
    private LocalDateTime expiryDate;

    @PrePersist
    void setExpiryDate() {
        this.expiryDate = LocalDateTime.now().plusDays(1);
    }
}
