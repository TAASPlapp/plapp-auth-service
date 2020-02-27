package com.plapp.authservice.services;

import com.plapp.entities.auth.UserCredentials;
import com.plapp.authservice.repositories.UserCredentialsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class SpringUserService implements UserDetailsService {
    private final UserCredentialsRepository userCredentialsRepository;

    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        UserCredentials credentials = userCredentialsRepository.findByEmail(email);

        if (credentials == null) {
            throw new UsernameNotFoundException(email);
        }

        return new User(credentials.getEmail(), credentials.getPassword(), new ArrayList<>());
    }
}
