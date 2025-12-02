package com.tuandatcoder.trekkerbackend.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/home")
    public String home(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return "User not authenticated";
        }
        String name = principal.getAttribute("name");
        return "Hello, " + name;
    }
    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }
}
