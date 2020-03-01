package com.plapp.authservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plapp.authservice.entities.ResourceAuthority;
import com.plapp.authservice.repositories.ResourceAuthorityRepository;
import com.plapp.authservice.security.JWTAuthenticationManager;
import com.plapp.authservice.repositories.UserCredentialsRepository;
import com.plapp.entities.auth.UserCredentials;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserCredentialsRepository userCredentialsRepository;
    private final AuthorizationService authorizationService;

    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public UserCredentials createUser(UserCredentials userCredentials) {
        if (userCredentialsRepository.findByEmail(userCredentials.getEmail()) != null)
            throw new IllegalArgumentException("Email already exists");

        userCredentials.setPassword(passwordEncoder.encode(userCredentials.getPassword()));
        UserCredentials savedCredentials = userCredentialsRepository.save(userCredentials);

        // Add default permission so that jwt will be able to later update
        // granted authorities
        authorizationService.addResourceAuthority(
                savedCredentials.getId(),
                "/auth/([0-9]+)/((\bupdate\b)|(\bremove))",
                new ArrayList<Long>(){{ add(savedCredentials.getId()); }});


        return savedCredentials;
    }

    public String authenticateUser(UserCredentials credentials) throws UsernameNotFoundException,
                                                                       BadCredentialsException,
                                                                       JsonProcessingException {
        if (credentials.getPassword() == null || credentials.getEmail() == null)
            throw new IllegalArgumentException("Missing credentials");


        // Throws exception if user is not found or credentials are invalid
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                credentials.getEmail(),
                credentials.getPassword()
        ));

        //Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        UserCredentials existingUser = userCredentialsRepository.findByEmail(credentials.getEmail());
        return authorizationService.buildJwtWithAuthorizations(existingUser.getId());
    }
}
