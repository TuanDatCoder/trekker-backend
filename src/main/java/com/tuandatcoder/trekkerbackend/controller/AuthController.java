package com.tuandatcoder.trekkerbackend.controller;

import com.tuandatcoder.trekkerbackend.dto.auth.request.LoginRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.auth.request.RegisterRequestDTO;
import com.tuandatcoder.trekkerbackend.entity.Account;
import com.tuandatcoder.trekkerbackend.enums.AccountStatusEnum;
import com.tuandatcoder.trekkerbackend.security.JwtTokenUtil;
import com.tuandatcoder.trekkerbackend.service.AccountService;
import com.tuandatcoder.trekkerbackend.service.EmailService;
import com.tuandatcoder.trekkerbackend.service.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequestDTO registerRequest) {
        Account newAccount = accountService.registerNewAccount(registerRequest);
        if (newAccount.getStatus() == AccountStatusEnum.UNVERIFIED) {
            String verificationToken = jwtTokenUtil.generateToken(new org.springframework.security.core.userdetails.User(newAccount.getEmail(), "", new ArrayList<>()));
            String verificationLink = "http://localhost:8080/auth/verify/" + verificationToken;
            emailService.sendVerificationEmail(newAccount.getEmail(), newAccount.getName(), verificationLink);
        }
        return new ResponseEntity<>("Đăng ký thành công, vui lòng kiểm tra email để xác thực tài khoản.", HttpStatus.CREATED);
    }

    @GetMapping("/verify/{token}")
    public ResponseEntity<String> verifyAccount(@PathVariable String token) {
        accountService.verifyAccountByToken(token);
        return new ResponseEntity<>("Xác thực tài khoản thành công.", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequestDTO loginRequest) {
        Map<String, Object> response = accountService.login(loginRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/logout")
    public String logout() {
        return "Logout successful";
    }
}