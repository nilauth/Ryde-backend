package com.cosmos.usersmanagementsystem.repository;

import com.cosmos.usersmanagementsystem.entity.ForgotPassword;
import com.cosmos.usersmanagementsystem.entity.OurUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword, Integer> {
    @Query("select fp from ForgotPassword fp where fp.otp = ?1 and fp.ourUser = ?2")
    Optional<ForgotPassword> findByOtpAndUser(Integer otp, OurUsers ourUser);
}
