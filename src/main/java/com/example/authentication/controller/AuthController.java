package com.example.authentication.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.example.authentication.dto.LoginRequest;
import com.example.authentication.dto.RegisterRequest;
import com.example.authentication.service.AuthService;

import jakarta.validation.Valid;

@Controller
public class AuthController {
	
	private final AuthService authService;
	

    public AuthController(AuthService authService) {
		this.authService = authService;
	}
    
    @GetMapping("/")
    public String home() {
        return "redirect:/register";
    }

	@GetMapping("/login")
    public String loginPage(Model model) {
        System.out.println("LOGIN...");
        model.addAttribute("loginRequest", new LoginRequest());

        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {

        model.addAttribute("registerRequest", new RegisterRequest());

        return "register";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
    
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute RegisterRequest registerRequest,
                               BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "register";
        }

        authService.register(registerRequest);

        // BUGFIX: purpose=REGISTER added so /verify-otp knows which OTP flow this is
        // (previously only email was passed, leaving purpose null downstream).
        return "redirect:/verify-otp?email=" + registerRequest.getEmail() + "&purpose=REGISTER";
    }

    @PostMapping("/login")
    public String loginUser(@Valid @ModelAttribute LoginRequest loginRequest,
                            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "login";
        }

         authService.login(loginRequest);

        // BUGFIX: purpose=LOGIN added, same reason as in registerUser() above.
        return "redirect:/verify-otp?email=" + loginRequest.getEmail() + "&purpose=LOGIN";
    }
}