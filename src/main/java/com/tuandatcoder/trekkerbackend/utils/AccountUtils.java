package com.tuandatcoder.trekkerbackend.utils;

import com.tuandatcoder.trekkerbackend.entity.Account;
import com.tuandatcoder.trekkerbackend.exception.ApiException;
import com.tuandatcoder.trekkerbackend.exception.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AccountUtils {

    public Account getCurrentAccount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Account) {
            return (Account) auth.getPrincipal();
        }
        return null;
    }

    // Dùng cái này trong service sẽ tiện hơn
    public Account getCurrentAccountOrThrow() {
        Account account = getCurrentAccount();
        if (account == null) {
            throw new ApiException("Unauthorized", ErrorCode.UNAUTHENTICATED);
        }
        return account;
    }
}