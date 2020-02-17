package com.plapp.authservice.controllers;

import com.plapp.authservice.entity.UserCredentials;
import com.plapp.authservice.repositories.UserCredentialsRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private BCryptPasswordEncoder passwordEncoder;
    private UserCredentialsRepository userCredentialsRepository;

    public AuthenticationController(UserCredentialsRepository userCredentialsRepository,
                                    BCryptPasswordEncoder passwordEncoder) {
        this.userCredentialsRepository = userCredentialsRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @PostMapping("/signup")
    public String signUp(@RequestBody UserCredentials credentials) {
        System.out.println("Signing up user: " + credentials.getEmail() + " " + credentials.getPassword());
        credentials.setPassword(passwordEncoder.encode(credentials.getPassword()));
        userCredentialsRepository.save(credentials);

        return "ok";
    }
}
