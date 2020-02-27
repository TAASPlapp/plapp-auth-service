package com.plapp.authservice.controllers;


import com.plapp.authservice.services.UserCredentialsService;
import com.plapp.entities.auth.UserCredentials;
import com.plapp.entities.utils.ApiResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.hibernate.HibernateError;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserCredentialsService userCredentialsService;

    @PostMapping("/signup")
    public ApiResponse signUp(@RequestBody UserCredentials credentials) {

        try {
            userCredentialsService.createUser(credentials);
        } catch (IllegalArgumentException e) {
            return new ApiResponse(false, e.getMessage());
        } catch (HibernateError e) {
            return new ApiResponse(false, "Could not create user:" + e.getMessage());
        }

        return new ApiResponse();
    }

    @PostMapping("/login")
    public ApiResponse logIn(@RequestBody UserCredentials credentials) {
        String jwt = null;

        try {
            jwt = userCredentialsService.authenticateUser(credentials);
        } catch (UsernameNotFoundException | BadCredentialsException e) {
            // Best if we dont reveal the email exists in our db
            return new ApiResponse(false, "Invalid credentials");
        }

        return new ApiResponse(true, jwt);
    }


    @PostMapping("/authorize")
    public Claims authorize(@RequestBody String jwt) {
        try {
            return userCredentialsService.verifyJWT(jwt);
        } catch (SignatureException e) {
            return null;
        }
    }
}
