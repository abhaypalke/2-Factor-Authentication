package com.example.authentication.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.authentication.dto.ForgotPasswordRequest;
import com.example.authentication.dto.ResetPasswordRequest;
import com.example.authentication.service.PasswordResetService;

import jakarta.validation.Valid;

@Controller
public class PasswordController {
	
	private final PasswordResetService passwordResetService;
	
    public PasswordController(PasswordResetService passwordResetService) {
		this.passwordResetService = passwordResetService;
	}

	@GetMapping("/forgot-password")
    public String forgotPasswordPage(Model model) {

        model.addAttribute("forgotPasswordRequest", new ForgotPasswordRequest());

        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(
            @Valid @ModelAttribute ForgotPasswordRequest forgotPasswordRequest,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "forgot-password";
        }

         passwordResetService.sendResetLink(forgotPasswordRequest);

        return "redirect:/login";
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam("token") String token,
                                    Model model) {

        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken(token);

        model.addAttribute("resetPasswordRequest", request);

        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(
            @Valid @ModelAttribute ResetPasswordRequest resetPasswordRequest,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "reset-password";
        }

         passwordResetService.resetPassword(resetPasswordRequest);

        return "redirect:/login";
    }
}