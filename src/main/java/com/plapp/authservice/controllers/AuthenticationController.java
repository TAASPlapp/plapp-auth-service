package com.plapp.authservice.controllers;


import com.plapp.authservice.services.UserCredentialsService;
import com.plapp.entities.auth.UserCredentials;
import com.plapp.entities.utils.ApiResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.hibernate.HibernateError;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
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
    public ApiResponse<UserCredentials> signUp(@RequestBody UserCredentials credentials) {

        try {
            UserCredentials savedUserCredentials = userCredentialsService.createUser(credentials);
            // not really a good idea to send the password back
            savedUserCredentials.setPassword("");
            return new ApiResponse<>(savedUserCredentials);
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(false, e.getMessage());
        } catch (HibernateError e) {
            return new ApiResponse<>(false, "Could not create user:" + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ApiResponse<String> logIn(@RequestBody UserCredentials credentials) {
        try {
            String jwt = userCredentialsService.authenticateUser(credentials);
            return new ApiResponse<>(jwt);

        } catch (UsernameNotFoundException | BadCredentialsException e) {
            // Best if we dont reveal the email exists in our db
            return new ApiResponse<>(false, "Invalid credentials");
        }
    }


    /*@PostMapping("/authorize")
    public ApiResponse authorize(@RequestBody String jwt) {
        try {
            userCredentialsService.verifyJwt(jwt);
            return new ApiResponse();

        } catch (JwtException e) {
            return new ApiResponse(false, "Invalid token");
        }
    }*/
}
