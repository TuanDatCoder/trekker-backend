package com.tuandatcoder.trekkerbackend.service;


import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.account.response.AccountResponseDTO;
import com.tuandatcoder.trekkerbackend.dto.auth.request.LoginRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.auth.request.RegisterRequestDTO;
import com.tuandatcoder.trekkerbackend.entity.Account;
import com.tuandatcoder.trekkerbackend.enums.AccountProviderEnum;
import com.tuandatcoder.trekkerbackend.enums.AccountRoleEnum;
import com.tuandatcoder.trekkerbackend.enums.AccountStatusEnum;
import com.tuandatcoder.trekkerbackend.exception.ErrorCode;
import com.tuandatcoder.trekkerbackend.exception.account.AccountException;
import com.tuandatcoder.trekkerbackend.repository.AccountRepository;
import com.tuandatcoder.trekkerbackend.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    public Account registerNewAccount(RegisterRequestDTO registerRequest) {
        if (accountRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new AccountException("User existed", ErrorCode.USER_EXISTED);
        }
        if (accountRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new AccountException("Username already taken", ErrorCode.USERNAME_TAKEN);
        }

        Account account = Account.builder()
                .email(registerRequest.getEmail())
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .name(registerRequest.getName())
                .gender(registerRequest.getGender())
                .dateOfBirth(registerRequest.getDateOfBirth())
                .provider(AccountProviderEnum.LOCAL)
                .role(AccountRoleEnum.USER)
                .status(AccountStatusEnum.UNVERIFIED)
                .createAt(LocalDateTime.now())
                .build();

        return accountRepository.save(account);
    }

    public void verifyAccountByToken(String token) {
        try {
            String email = jwtTokenUtil.getUsernameFromToken(token);
            verifyAccountByEmail(email);
        } catch (Exception e) {
            throw new AccountException("Invalid or expired token", ErrorCode.TOKEN_INVALID);
        }
    }

    public void verifyAccountByEmail(String email) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AccountException("Account not found", ErrorCode.ACCOUNT_NOT_FOUND));

        if (account.getStatus() == AccountStatusEnum.VERIFIED) {
            return; // đã verify rồi thì thôi
        }

        account.setStatus(AccountStatusEnum.VERIFIED);
        accountRepository.save(account);
    }

    // TRẢ VỀ ApiResponse<Map<String, Object>>
    public ApiResponse<Map<String, Object>> login(LoginRequestDTO loginRequest) {
        String identifier = loginRequest.getIdentifier();

        Account account = accountRepository.findByEmail(identifier)
                .or(() -> accountRepository.findByUsername(identifier))
                .orElseThrow(() -> new AccountException(
                        "User not found with provided email or username",
                        ErrorCode.USER_NOT_FOUND
                ));

        // Kiểm tra provider
        if (account.getProvider() != AccountProviderEnum.LOCAL) {
            return new ApiResponse<>(400, "Please log in using " + account.getProvider(), null);
        }

        // Kiểm tra đã verify chưa
        if (account.getStatus() != AccountStatusEnum.VERIFIED) {
            return new ApiResponse<>(403, "Please verify your email first", null);
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(identifier, loginRequest.getPassword())
            );

            UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(identifier);
            String token = jwtTokenUtil.generateToken(userDetails);

            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("userId", account.getId());
            data.put("username", account.getUsername());
            data.put("email", account.getEmail());
            data.put("name", account.getName());

            return new ApiResponse<>(200, "Login successful", data);

        } catch (BadCredentialsException e) {
            return new ApiResponse<>(401, "Invalid password", null);
        } catch (Exception e) {
            return new ApiResponse<>(500, "Login failed: " + e.getMessage(), null);
        }
    }


    public ApiResponse<List<AccountResponseDTO>> getAllAccounts() {
        try {
            List<Account> accounts = accountRepository.findAll();
            List<AccountResponseDTO> dtos = accounts.stream()
                    .map(this::convertToDto)  // ← QUAN TRỌNG: Chỉ expose field cần thiết
                    .collect(Collectors.toList());

            return new ApiResponse<>(200, "Lấy danh sách tài khoản thành công", dtos);
        } catch (Exception e) {
            return new ApiResponse<>(500, "Lỗi khi lấy tài khoản: " + e.getMessage(), null);
        }
    }

    private AccountResponseDTO convertToDto(Account account) {
        // CHỈ expose những field cần thiết cho frontend
        AccountResponseDTO dto = new AccountResponseDTO();
        dto.setId(account.getId());
        dto.setUsername(account.getUsername()); // ← ĐÚNG! username, không phải name
        dto.setEmail(account.getEmail());
        dto.setName(account.getName());
        dto.setRole(account.getRole());
        dto.setStatus(account.getStatus());
        // KHÔNG set password, createdAt, updatedAt...

        return dto;
    }
}