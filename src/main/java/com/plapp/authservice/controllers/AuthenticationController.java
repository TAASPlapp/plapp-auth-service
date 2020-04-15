package com.plapp.authservice.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.plapp.authservice.services.AuthenticationService;
import com.plapp.entities.auth.UserCredentials;
import com.plapp.entities.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.hibernate.HibernateError;
import org.hibernate.HibernateException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService userCredentialsService;

    @ControllerAdvice
    public static class AuthenticationControllerAdvice extends ResponseEntityExceptionHandler {
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        @ExceptionHandler({IllegalArgumentException.class})
        public void handleIllegalArgumentException() { }

        /*@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        @ExceptionHandler({HibernateException.class})
        public void handle() {}*/
    }

    @PostMapping("/signup")
    public UserCredentials signUp(@RequestBody UserCredentials credentials) {
        UserCredentials savedUserCredentials = userCredentialsService.createUser(credentials);

        // not really a good idea to send the password back
        savedUserCredentials.setPassword("");
        return savedUserCredentials;
    }

    @PostMapping("/login")
    public String logIn(@RequestBody UserCredentials credentials) {
        try {
            return userCredentialsService.authenticateUser(credentials);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
