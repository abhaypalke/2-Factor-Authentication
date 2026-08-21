package com.example.authentication.util;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

@Component
public class OtpGenerator {
	
	private static final SecureRandom Random = new SecureRandom();
	
	private OtpGenerator() {
		
	}
	
	public String generateOtp() {
		return String.format("%06d", Random.nextInt(1_000_000));
	}

}
