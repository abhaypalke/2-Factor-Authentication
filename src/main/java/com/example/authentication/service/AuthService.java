package com.example.authentication.service;

import com.example.authentication.dto.LoginRequest;
import com.example.authentication.dto.RegisterRequest;

public interface AuthService {

    void register(RegisterRequest request);

    void login(LoginRequest request);
}