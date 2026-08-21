package com.example.authentication.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.authentication.entity.OtpPurpose;
import com.example.authentication.entity.OtpVerification;
import com.example.authentication.entity.User;

public interface OtpVerificationRepository extends JpaRepository<OtpVerification , Long>{

	Optional<OtpVerification> findByUserAndPurposeAndVerifiedFalse(
	        User user,
	        OtpPurpose purpose
	);
	
}
