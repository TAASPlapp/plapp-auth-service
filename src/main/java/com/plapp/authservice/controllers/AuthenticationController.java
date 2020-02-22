package com.plapp.authservice.controllers;

import com.plapp.authservice.repositories.UserCredentialsRepository;
import com.plapp.authservice.security.JWTAuthenticationManager;
import com.plapp.entities.auth.UserCredentials;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTAuthenticationManager jwtAuthenticationManager;

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
        if (credentials.getPassword() == null || credentials.getEmail() == null)
            return new ApiResponse(false, "Missing credentials");

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    credentials.getEmail(),
                    credentials.getPassword()
            ));
        } catch (UsernameNotFoundException | BadCredentialsException e) {
            // Best if we dont reveal the email exists in our db
            return new ApiResponse(false, "Invalid credentials");
        }

        UserCredentials existingUser = userCredentialsRepository.findByEmail(credentials.getEmail());
        String JWT = jwtAuthenticationManager.buildJWT(existingUser);

        return new ApiResponse(true, JWT);
    }


    @PostMapping("/authorize")
    public Claims authorize(@RequestBody String jwt) {
        return jwtAuthenticationManager.verifyJWT(jwt);
    }
}
