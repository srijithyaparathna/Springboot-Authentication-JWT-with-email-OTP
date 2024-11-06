package com.RNR_CarSaleApp.carsale.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.domain.Auditable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Entity
@Table(name = "tbl_users")
@Data
public class Users implements Auditable<String, Integer, Instant>, UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String email;

    private String name;
    private String password;
    private String accountVerified;
    private String loginDisabled; // Keep this as String for compatibility with DB

    private String role;

    // Maintain a set of OTP tokens associated with this user
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")  // Assuming the relationship will be based on user_id in the OTP table
    private Set<OTPVerification> otpVerifications;

    public boolean isLoginDisabled() {
        return "true".equalsIgnoreCase(loginDisabled); // Return boolean based on string value
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role)); // Return authorities based on role
    }

    @Override
    public String getUsername() {
        return email; // Return the email as the username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // User accounts are non-expired
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // User accounts are non-locked
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // User credentials are non-expired
    }

    @Override
    public boolean isEnabled() {
        return !"true".equalsIgnoreCase(loginDisabled); // Consider loginDisabled status for enabled status
    }

    // Implement Auditable methods with appropriate return types
    @Override
    public Optional<String> getCreatedBy() {
        return Optional.empty(); // Placeholder for created by user
    }

    @Override
    public Optional<Instant> getCreatedDate() {
        return null; // Return actual creation date if available
    }

    @Override
    public void setCreatedDate(Instant creationDate) {
        // Implement as necessary to store the creation date
    }

    @Override
    public Optional<String> getLastModifiedBy() {
        return Optional.empty(); // Placeholder for last modified by user
    }

    @Override
    public Optional<Instant> getLastModifiedDate() {
        return null; // Return actual last modified date if available
    }

    @Override
    public void setLastModifiedDate(Instant lastModifiedDate) {
        // Implement as necessary to store the last modified date
    }

    @Override
    public void setLastModifiedBy(String lastModifiedBy) {
        // Implement as necessary to store the last modified by user
    }

    @Override
    public void setCreatedBy(String createdBy) {
        // Implement as necessary to store the created by user
    }

    @Override
    public boolean isNew() {
        return false; // Implement logic to determine if the user is new
    }
}
