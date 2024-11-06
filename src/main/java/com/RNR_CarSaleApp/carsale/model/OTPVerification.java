package com.RNR_CarSaleApp.carsale.model;

import lombok.Data;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "otp_verifications")  // Updated table name to reflect OTP usage
public class OTPVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String otp;  // OTP field instead of token

    @Column(updatable = false)
    @Basic(optional = false)
    private LocalDateTime expiredAt;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Users user;  // Renamed users to user for clarity

    // Field to mark OTP verification status
    @Column(nullable = false)
    private boolean isVerified = false;

    // Checks if the OTP has expired by comparing expiredAt with the current time
    public boolean isExpired() {
        return expiredAt.isBefore(LocalDateTime.now());
    }

    // Returns the expiry time as Instant
    public Instant getExpiryTime() {
        return expiredAt.atZone(java.time.ZoneId.systemDefault()).toInstant();
    }
}
