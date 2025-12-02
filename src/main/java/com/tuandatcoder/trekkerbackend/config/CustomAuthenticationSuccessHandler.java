package com.tuandatcoder.trekkerbackend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.account.response.AccountResponseDTO;
import com.tuandatcoder.trekkerbackend.dto.auth.response.LoginResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.Account;
import com.tuandatcoder.trekkerbackend.enums.AccountProviderEnum;
import com.tuandatcoder.trekkerbackend.enums.AccountRoleEnum;
import com.tuandatcoder.trekkerbackend.enums.AccountStatusEnum;
import com.tuandatcoder.trekkerbackend.repository.AccountRepository;
import com.tuandatcoder.trekkerbackend.security.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final AccountRepository accountRepository;
    private final JwtTokenUtil jwtTokenUtil;

    public CustomAuthenticationSuccessHandler(AccountRepository accountRepository, JwtTokenUtil jwtTokenUtil) {
        this.accountRepository = accountRepository;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");

        try {
            Optional<Account> accountOpt = accountRepository.findByEmail(email);
            boolean isNewAccount = accountOpt.isEmpty();

            Account account;
            if (isNewAccount) {
                account = Account.builder()
                        .email(email)
                        .name((String) attributes.get("name"))
                        .picture((String) attributes.get("picture"))
                        .googleId((String) attributes.get("sub"))
                        .role(AccountRoleEnum.USER)
                        .provider(AccountProviderEnum.GOOGLE)
                        .status(AccountStatusEnum.VERIFIED)
                        .createAt(LocalDateTime.now())
                        .build();
                account = accountRepository.save(account);
            } else {
                account = accountOpt.get();
            }

            // Tạo UserDetails để generate token
            UserDetails userDetails = new User(
                    account.getEmail(),
                    "",
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + account.getRole().name()))
            );

            String accessToken = jwtTokenUtil.generateToken(userDetails);
            String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);

            // Convert Account → DTO
            AccountResponseDTO userDto = toAccountResponseDTO(account);

            // Tạo data response
            LoginResponseDTO loginData = new LoginResponseDTO(accessToken, refreshToken, userDto);

            // Message tùy theo trường hợp
            String message = isNewAccount
                    ? "Registered and logged in successfully with Google!"
                    : "Successfully logged in with Google";

            ApiResponse<LoginResponseDTO> apiResponse = new ApiResponse<>(200, message, loginData);

            response.getWriter().write(objectMapper.writeValueAsString(apiResponse));

        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse<LoginResponseDTO> errorResponse = new ApiResponse<>(
                    500,
                    "Google Sign In Failed: " + e.getMessage(),
                    null
            );
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }
    }

    // Helper: convert Account → DTO
    private AccountResponseDTO toAccountResponseDTO(Account account) {
        AccountResponseDTO dto = new AccountResponseDTO();
        dto.setId(account.getId());
        dto.setEmail(account.getEmail());
        dto.setUsername(account.getUsername());
        dto.setName(account.getName());
        dto.setPicture(account.getPicture());
        dto.setRole(account.getRole());
        dto.setStatus(account.getStatus());
        return dto;
    }
}