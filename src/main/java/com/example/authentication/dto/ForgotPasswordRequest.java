package com.example.authentication.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForgotPasswordRequest {

	@Email(message ="Please Enter Valid Email")
	@NotBlank(message = "Email is Required")
	private String email;
}
