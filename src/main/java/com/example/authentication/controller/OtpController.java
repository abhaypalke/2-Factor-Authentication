package com.example.authentication.controller;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.authentication.dto.OtpRequest;
import com.example.authentication.entity.OtpPurpose;
import com.example.authentication.service.OtpService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
public class OtpController {
	
	private final OtpService otpService;

	// BUGFIX (login stuck in a redirect loop / 403 on dashboard): Spring Security 6's
	// default SecurityContextHolderFilter only LOADS the SecurityContext from the
	// session at the start of a request; unlike the pre-6.0 SecurityContextPersistenceFilter,
	// it does NOT automatically save it back afterwards. OtpServiceImpl sets the
	// authenticated user on SecurityContextHolder after a successful LOGIN-purpose OTP,
	// but without an explicit save that authentication only lives for the remainder of
	// that single request - the next request (GET /dashboard) starts anonymous again,
	// so Spring Security 403s it. This repository is used below to persist the context
	// to the HttpSession explicitly, right where the request/response are available.
	private final SecurityContextRepository securityContextRepository =
			new HttpSessionSecurityContextRepository();

    public OtpController(OtpService otpService) {
		this.otpService = otpService;
	}

	@GetMapping("/verify-otp")
    public String verifyOtpPage(
            // BUGFIX: this page is only ever reached via a redirect that carries
            // ?email=...&purpose=..., but the old code ignored both params and
            // dropped a blank OtpRequest into the model. The JSP's hidden
            // <form:hidden path="email"/> / <form:hidden path="purpose"/> fields
            // then rendered empty, so every OTP submission failed @NotBlank
            // validation on email before verifyOtp() was ever called.
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "purpose", required = false) OtpPurpose purpose,
            Model model) {

        OtpRequest otpRequest = new OtpRequest();
        otpRequest.setEmail(email);
        otpRequest.setPurpose(purpose);

        model.addAttribute("otpRequest", otpRequest);

        return "verify-otp";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@Valid @ModelAttribute OtpRequest otpRequest,
                            BindingResult bindingResult,
                            HttpServletRequest request,
                            HttpServletResponse response) {

        if (bindingResult.hasErrors()) {
            return "verify-otp";
        }

         otpService.verifyOtp(otpRequest);

        // BUGFIX: REGISTER-purpose OTPs only activate the account, they do not
        // log the user in (see OtpServiceImpl). Only LOGIN-purpose OTPs result
        // in an authenticated session. Previously this always redirected to
        // /dashboard, which is a protected route -> a freshly registered,
        // still-unauthenticated user was redirected somewhere they could never
        // reach. Send REGISTER users to /login instead.
        if (otpRequest.getPurpose() == OtpPurpose.LOGIN) {
            // BUGFIX: persist the authentication that OtpServiceImpl just set on
            // SecurityContextHolder into the HTTP session, so it's still there on
            // the very next request (the redirect to /dashboard below). Without
            // this line, /dashboard always came back 403 (see class-level comment).
            SecurityContext context = SecurityContextHolder.getContext();
            securityContextRepository.saveContext(context, request, response);

            return "redirect:/dashboard";
        }
        return "redirect:/login";
    }

    @PostMapping("/resend-otp")
    public String resendOtp(@ModelAttribute OtpRequest otpRequest) {

         otpService.resendOtp(otpRequest.getEmail(),
        		 otpRequest.getPurpose());

        // BUGFIX: purpose was previously dropped from this redirect too,
        // which would have blanked the hidden "purpose" field again.
        return "redirect:/verify-otp?email=" + otpRequest.getEmail()
                + "&purpose=" + otpRequest.getPurpose();
    }
}