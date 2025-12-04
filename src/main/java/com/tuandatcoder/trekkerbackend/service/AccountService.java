package com.tuandatcoder.trekkerbackend.service;


import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.account.response.AccountResponseDTO;
import com.tuandatcoder.trekkerbackend.dto.auth.request.LoginRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.auth.request.RegisterRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.auth.response.LoginResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.Account;
import com.tuandatcoder.trekkerbackend.enums.AccountProviderEnum;
import com.tuandatcoder.trekkerbackend.enums.AccountRoleEnum;
import com.tuandatcoder.trekkerbackend.enums.AccountStatusEnum;
import com.tuandatcoder.trekkerbackend.exception.ApiException;
import com.tuandatcoder.trekkerbackend.exception.ErrorCode;
import com.tuandatcoder.trekkerbackend.mapper.AccountMapper;
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
import java.util.List;
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

    @Autowired
    private AccountMapper accountMapper;

    public Account registerNewAccount(RegisterRequestDTO registerRequest) {
        if (accountRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new ApiException("User existed", ErrorCode.USER_EXISTED);
        }
        if (accountRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new ApiException("Username already taken", ErrorCode.USERNAME_TAKEN);
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
            String email = jwtTokenUtil.getEmailFromToken(token);
            verifyAccountByEmail(email);
        } catch (Exception e) {
            throw new ApiException("Invalid or expired token", ErrorCode.TOKEN_INVALID);
        }
    }

    public void verifyAccountByEmail(String email) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("Account not found", ErrorCode.ACCOUNT_NOT_FOUND));

        if (account.getStatus() == AccountStatusEnum.VERIFIED) {
            return; // đã verify rồi thì thôi
        }

        account.setStatus(AccountStatusEnum.VERIFIED);
        accountRepository.save(account);
    }

    public ApiResponse<LoginResponseDTO> login(LoginRequestDTO loginRequest) {

        String identifier = loginRequest.getIdentifier();

        Account account = accountRepository.findByEmail(identifier)
                .or(() -> accountRepository.findByUsername(identifier))
                .orElseThrow(() ->
                        new ApiException("User not found with provided email or username",
                                ErrorCode.USER_NOT_FOUND)
                );

        if (account.getProvider() != AccountProviderEnum.LOCAL) {
            return new ApiResponse<>(400, "Please log in using " + account.getProvider(), null);
        }

        if (account.getStatus() != AccountStatusEnum.VERIFIED) {
            return new ApiResponse<>(403, "Please verify your email first", null);
        }

        try {
            // Validate password
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            identifier,
                            loginRequest.getPassword()
                    )
            );

            UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(identifier);

            String accessToken = jwtTokenUtil.generateToken(userDetails);
            String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);

            LoginResponseDTO responseData = new LoginResponseDTO(
                    accessToken,
                    refreshToken,
                    accountMapper.toDto(account)
            );

            return new ApiResponse<>(200, "Login successful", responseData);

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
                    .map(accountMapper::toDto)
                    .collect(Collectors.toList());

            return new ApiResponse<>(200, "Get list of successful accounts", dtos);
        } catch (Exception e) {
            return new ApiResponse<>(500, "Error retrieving account: " + e.getMessage(), null);
        }
    }
}