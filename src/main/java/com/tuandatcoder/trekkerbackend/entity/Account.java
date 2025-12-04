package com.tuandatcoder.trekkerbackend.entity;

import com.tuandatcoder.trekkerbackend.enums.AccountProviderEnum;
import com.tuandatcoder.trekkerbackend.enums.AccountRoleEnum;
import com.tuandatcoder.trekkerbackend.enums.AccountStatusEnum;
import com.tuandatcoder.trekkerbackend.enums.AccountGenderEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "account")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(unique = true, nullable = false, length = 150)
    private String email;

    @Column(length = 255)
    private String password;

    @Column(name = "google_id", unique = true)
    private String googleId;

    // --- Thông tin cá nhân mới thêm ---
    @Column(length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String picture; // Avatar URL

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private AccountGenderEnum gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(length = 20)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String address;
    // ----------------------------------

    // --- Enum quản lý trạng thái ---
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default // Mặc định là USER nếu không set
    private AccountRoleEnum role = AccountRoleEnum.USER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AccountProviderEnum provider = AccountProviderEnum.LOCAL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AccountStatusEnum status = AccountStatusEnum.UNVERIFIED;

    @CreationTimestamp
    @Column(name = "create_at", updatable = false)
    private LocalDateTime createAt;
}