package com.tuandatcoder.trekkerbackend.service;

import com.tuandatcoder.trekkerbackend.entity.Account;
import com.tuandatcoder.trekkerbackend.enums.AccountStatusEnum;
import com.tuandatcoder.trekkerbackend.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(identifier)
                .or(() -> accountRepository.findByUsername(identifier))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + identifier));


        if (account.getStatus() != AccountStatusEnum.VERIFIED) {
            throw new UsernameNotFoundException("Account not verified yet");
        }

        var authority = new SimpleGrantedAuthority("ROLE_" + account.getRole().name());

        return new User(
                account.getEmail(),
                account.getPassword(),
                Collections.singletonList(authority)
        );
    }
}