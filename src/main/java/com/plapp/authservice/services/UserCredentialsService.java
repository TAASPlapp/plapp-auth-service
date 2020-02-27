package com.plapp.authservice.services;

import com.plapp.authservice.security.JWTAuthenticationManager;
import com.plapp.entities.auth.UserCredentials;
import com.plapp.authservice.repositories.UserCredentialsRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class UserCredentialsService  {
    private final UserCredentialsRepository userCredentialsRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTAuthenticationManager jwtAuthenticationManager;

    public UserCredentials createUser(UserCredentials userCredentials) {
        if (userCredentialsRepository.findByEmail(userCredentials.getEmail()) != null)
            throw new IllegalArgumentException("Email already exists");

        userCredentials.setPassword(passwordEncoder.encode(userCredentials.getPassword()));
        return userCredentialsRepository.save(userCredentials);
    }

    public String authenticateUser(UserCredentials credentials) throws UsernameNotFoundException,
                                                                       BadCredentialsException {
        if (credentials.getPassword() == null || credentials.getEmail() == null)
            throw new IllegalArgumentException("Missing credentials");


        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                credentials.getEmail(),
                credentials.getPassword()
        ));

        UserCredentials existingUser = userCredentialsRepository.findByEmail(credentials.getEmail());
        return jwtAuthenticationManager.buildJWT(existingUser);
    }

    public Claims verifyJWT(String jwt) {
        return jwtAuthenticationManager.verifyJWT(jwt);
    }
}
