package com.plapp.authservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plapp.authservice.entities.ResourceAuthority;
import com.plapp.authservice.entities.UserCredentialsDPO;
import com.plapp.authservice.security.JWTAuthenticationManager;
import com.plapp.authservice.repositories.UserCredentialsRepository;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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

    private ObjectMapper objectMapper = new ObjectMapper();

    public UserCredentialsDPO createUser(UserCredentialsDPO userCredentials) {
        if (userCredentialsRepository.findByEmail(userCredentials.getEmail()) != null)
            throw new IllegalArgumentException("Email already exists");

        userCredentials.setPassword(passwordEncoder.encode(userCredentials.getPassword()));
        UserCredentialsDPO savedCredentials = userCredentialsRepository.save(userCredentials);


        try {
            System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(savedCredentials));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        ResourceAuthority writeCredentialsAuthority = new ResourceAuthority();
        writeCredentialsAuthority.setAuthority("/auth/([0-9]+)/((\bupdate\b)|(\bremove))");
        writeCredentialsAuthority.addValue(savedCredentials.getId());
        savedCredentials.addResourceAuthority(writeCredentialsAuthority);


        UserCredentialsDPO updatedCredentials = userCredentialsRepository.save(savedCredentials);
        try {
            System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(updatedCredentials));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return updatedCredentials;
    }

    public String authenticateUser(UserCredentialsDPO credentials) throws UsernameNotFoundException,
                                                                       BadCredentialsException {
        if (credentials.getPassword() == null || credentials.getEmail() == null)
            throw new IllegalArgumentException("Missing credentials");


        // Throws exception if user is not found or credentials are invalid
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                credentials.getEmail(),
                credentials.getPassword()
        ));

        System.out.println("This motherfucker has authorities: ");
        try {
            System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(authentication));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        UserCredentialsDPO existingUser = userCredentialsRepository.findByEmail(credentials.getEmail());
        return jwtAuthenticationManager.buildJWT(existingUser);
    }

    public void verifyJwt(String jwt) throws JwtException {
        jwtAuthenticationManager.verifyJwt(jwt);
    }
}
