package com.plapp.authservice.controllers;

import com.plapp.authservice.entity.UserCredentials;
import com.plapp.authservice.repositories.UserCredentialsRepository;
import com.plapp.authservice.services.UserCredentialsService;
import org.lists.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private BCryptPasswordEncoder passwordEncoder;
    private UserCredentialsRepository userCredentialsRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserCredentialsService userCredentialsService;

    public AuthenticationController(UserCredentialsRepository userCredentialsRepository,
                                    BCryptPasswordEncoder passwordEncoder) {
        this.userCredentialsRepository = userCredentialsRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean userExists(UserCredentials credentials) {
        System.out.println("Checking if user " + credentials.getEmail() + " exists");
        UserCredentials existing = userCredentialsRepository.findByEmail(credentials.getEmail());
        System.out.println(existing);

        return existing != null;
    }

    @PostMapping("/signup")
    public ApiResponse signUp(@RequestBody UserCredentials credentials) {
        if (userExists(credentials)) {
            return new ApiResponse(false, "User already exists");
        }

        System.out.println("Signing up user: " + credentials.getEmail() + " " + credentials.getPassword());
        credentials.setPassword(passwordEncoder.encode(credentials.getPassword()));
        userCredentialsRepository.save(credentials);

        return new ApiResponse();
    }

    @PostMapping("/login")
    public ApiResponse logIn(@RequestBody UserCredentials credentials) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    credentials.getEmail(),
                    credentials.getPassword()
            ));
        } catch (UsernameNotFoundException | BadCredentialsException e) {
            // Best if we dont reveal the email exists in our db
            return new ApiResponse(false, "Invalid credentials");
        }

        // Generate JWT token here
        String JWT = "asdfghj";

        return new ApiResponse(true, "Bearer " + JWT);
    }
}
