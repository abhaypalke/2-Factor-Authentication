package com.example.authentication.util;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class TokenGenerator {

	private TokenGenerator() {
		
	}
	
	public static String generateToken() {
		return UUID.randomUUID().toString();
	}
}
