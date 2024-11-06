package com.RNR_CarSaleApp.carsale.model;

import lombok.Data;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "secureTokens")
public class SecureToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String token;

    @Column(updatable = false)
    @Basic(optional = false)
    private LocalDateTime expiredAt;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Users users;

    // Checks if the token has expired by comparing expiredAt with the current time
    public boolean isExpired() {
        return expiredAt.isBefore(LocalDateTime.now());
    }
}
