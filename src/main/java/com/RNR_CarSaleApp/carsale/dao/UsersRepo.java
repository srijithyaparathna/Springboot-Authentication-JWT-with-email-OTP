package com.RNR_CarSaleApp.carsale.dao;

import com.RNR_CarSaleApp.carsale.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsersRepo extends JpaRepository<Users, Integer> {

    Optional<Users> findByEmail(String email); // Fetch a single user by email
    List<Users> findByRole(String role);       // Fetch users by role
    List<Users> findByNameContainingIgnoreCase(String name); // Search users by name (case-insensitive)
    List<Users> findAllByEmail(String email);

    // Combined query methods
    List<Users> findByNameContainingIgnoreCaseAndEmailAndRole(String name, String email, String role);
    List<Users> findByNameContainingIgnoreCaseAndEmail(String name, String email);
    List<Users> findByNameContainingIgnoreCaseAndRole(String name, String role);
    List<Users> findByEmailAndRole(String email, String role);
}
