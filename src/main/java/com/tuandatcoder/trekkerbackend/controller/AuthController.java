package com.tuandatcoder.trekkerbackend.controller;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.auth.request.LoginRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.auth.request.RegisterRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.auth.response.LoginResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.Account;
import com.tuandatcoder.trekkerbackend.enums.AccountStatusEnum;
import com.tuandatcoder.trekkerbackend.security.JwtTokenUtil;
import com.tuandatcoder.trekkerbackend.service.AccountService;
import com.tuandatcoder.trekkerbackend.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    // REGISTER
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@RequestBody RegisterRequestDTO registerRequest) {
        Account newAccount = accountService.registerNewAccount(registerRequest);

        if (newAccount.getStatus() == AccountStatusEnum.UNVERIFIED) {
            String verificationToken = jwtTokenUtil.generateToken(
                    new org.springframework.security.core.userdetails.User(newAccount.getEmail(), "", new ArrayList<>())
            );
            String verificationLink = "http://localhost:8080/api/auth/verify/" + verificationToken;
            emailService.sendVerificationEmail(newAccount.getEmail(), newAccount.getName(), verificationLink);
        }

        ApiResponse<String> response = new ApiResponse<>(
                201,
                "Registration successful. Please check your email to verify your account.",
                null

        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // VERIFY EMAIL
    @GetMapping("/verify/{token}")
    public ResponseEntity<ApiResponse<String>> verifyAccount(@PathVariable String token) {
        accountService.verifyAccountByToken(token);
        ApiResponse<String> response = new ApiResponse<>(200, "Account verified successfully.", null);
        return ResponseEntity.ok(response);
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(@RequestBody LoginRequestDTO loginRequest) {
        ApiResponse<LoginResponseDTO> response = accountService.login(loginRequest);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    // LOGOUT (nếu có xử lý backend, ví dụ blacklist token thì để đây)
    @GetMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout() {
        ApiResponse<String> response = new ApiResponse<>(200, "Logout successful", null);
        return ResponseEntity.ok(response);
    }
}