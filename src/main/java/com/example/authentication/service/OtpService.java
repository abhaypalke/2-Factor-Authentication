package com.example.authentication.service;

import com.example.authentication.dto.OtpRequest;
import com.example.authentication.entity.OtpPurpose;

public interface OtpService {

    void verifyOtp(OtpRequest request);

    void resendOtp(String email, OtpPurpose purpose);
}