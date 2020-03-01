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

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserCredentialsRepository userCredentialsRepository;
    private final ResourceAuthorityRepository resourceAuthorityRepository;

    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTAuthenticationManager jwtAuthenticationManager;

    private ObjectMapper objectMapper = new ObjectMapper();

    public UserCredentials createUser(UserCredentials userCredentials) {
        if (userCredentialsRepository.findByEmail(userCredentials.getEmail()) != null)
            throw new IllegalArgumentException("Email already exists");

        userCredentials.setPassword(passwordEncoder.encode(userCredentials.getPassword()));
        UserCredentials savedCredentials = userCredentialsRepository.save(userCredentials);


        try {
            System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(savedCredentials));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        ResourceAuthority writeCredentialsAuthority = new ResourceAuthority();
        writeCredentialsAuthority.setAuthority("/auth/([0-9]+)/((\bupdate\b)|(\bremove))");
        writeCredentialsAuthority.addValue(savedCredentials.getId());
        writeCredentialsAuthority.setUserId(savedCredentials.getId());
        resourceAuthorityRepository.save(writeCredentialsAuthority);

        return savedCredentials;
    }

    public String authenticateUser(UserCredentials credentials) throws UsernameNotFoundException,
                                                                       BadCredentialsException,
                                                                       JsonProcessingException {
        if (credentials.getPassword() == null || credentials.getEmail() == null)
            throw new IllegalArgumentException("Missing credentials");


        // Throws exception if user is not found or credentials are invalid
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                credentials.getEmail(),
                credentials.getPassword()
        ));

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        try {
            System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(authorities));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        UserCredentials existingUser = userCredentialsRepository.findByEmail(credentials.getEmail());
        return jwtAuthenticationManager.buildJWT(existingUser, authorities);
    }
}
