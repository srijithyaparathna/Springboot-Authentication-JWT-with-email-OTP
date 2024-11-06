package com.RNR_CarSaleApp.carsale.dao;

import com.RNR_CarSaleApp.carsale.model.OTPVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OTPVerificationRepository extends JpaRepository<OTPVerification, Integer> {
    Optional<OTPVerification> findByUser_EmailAndOtp(String email, String otp);
}
