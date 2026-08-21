package com.example.authentication.serviceImpl;

import java.time.LocalDateTime;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.authentication.dto.OtpRequest;
import com.example.authentication.entity.OtpPurpose;
import com.example.authentication.entity.OtpVerification;
import com.example.authentication.entity.User;
import com.example.authentication.exception.InvalidOtpException;
import com.example.authentication.exception.ResourceNotFoundException;
import com.example.authentication.repository.OtpVerificationRepository;
import com.example.authentication.repository.UserRepository;
import com.example.authentication.security.CustomUserDetailsService;
import com.example.authentication.service.MailService;
import com.example.authentication.service.OtpService;
import com.example.authentication.util.OtpGenerator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

	private final UserRepository userRepository;
	private final OtpVerificationRepository otpVerificationRepository;
	private final CustomUserDetailsService userDetailsService;
	private final MailService mailService;
	private final OtpGenerator otpGenerator;
	
	@Override
	public void verifyOtp(OtpRequest request) {
		
		System.out.println("veriy otp service called..............");
		
		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
		
		OtpVerification verification = otpVerificationRepository.findByUserAndPurposeAndVerifiedFalse(user, request.getPurpose())
																.orElseThrow(() ->
																 new InvalidOtpException("OTP not found"));
		
		if(verification.getExpiryTime().isBefore(LocalDateTime.now())) {
			throw new InvalidOtpException("OTP expired");
		}
		
		if (!verification.getOtp().equals(request.getOtp())) {
            throw new InvalidOtpException("Invalid OTP");
        }
		
		verification.setVerified(true);
		otpVerificationRepository.save(verification);
		
		  if (request.getPurpose() == OtpPurpose.REGISTER) {

	            user.setEnabled(true);
	            user.setEmailVerified(true);

	            userRepository.save(user);
	            System.out.println("user register successfully......");
	            
	        }

//		  || request.getPurpose() == OtpPurpose.REGISTER
		  
	      if (request.getPurpose() == OtpPurpose.LOGIN ) {

	            // Password was already verified in AuthServiceImpl.login() before the OTP
	            // was issued, and the OTP itself has just been verified above, so we build
	            // an already-authenticated token instead of re-checking the password here.
	            // (Re-checking against user.getPassword() — the BCrypt hash — as if it were
	            // a raw password is the bug this replaces: it can never match and always
	            // threw BadCredentialsException.)
	            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

	            Authentication authentication =
	                    new UsernamePasswordAuthenticationToken(
	                            userDetails,
	                            null,
	                            userDetails.getAuthorities()
	                    );

	            SecurityContextHolder.getContext()
	                    .setAuthentication(authentication);
	            
	            System.out.println("Authentication set done");
	        }
	}
		
	
	@Override
	public void resendOtp(String email, OtpPurpose purpose) {

	    User user = userRepository.findByEmail(email)
	            .orElseThrow(() ->
	                    new ResourceNotFoundException("User not found"));

	    OtpVerification otpVerification = otpVerificationRepository
	            .findByUserAndPurposeAndVerifiedFalse(user, purpose)
	            .orElseThrow(() ->
	                    new InvalidOtpException("No active OTP found"));

	    String otp = otpGenerator.generateOtp();

	    otpVerification.setOtp(otp);
	    otpVerification.setExpiryTime(LocalDateTime.now().plusMinutes(5));
	    otpVerification.setVerified(false);

	    otpVerificationRepository.save(otpVerification);

	    if (purpose == OtpPurpose.REGISTER) {
	        mailService.sendRegistrationOtp(
	                user.getEmail(),
	                user.getName(),
	                otp
	        );
	    } else {
	        mailService.sendLoginOtp(
	                user.getEmail(),
	                user.getName(),
	                otp
	        );
	    }
	}

}
