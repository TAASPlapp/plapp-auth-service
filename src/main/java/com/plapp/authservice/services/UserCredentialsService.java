package com.plapp.authservice.services;

import com.plapp.authservice.entity.UserCredentials;
import com.plapp.authservice.repositories.UserCredentialsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserCredentialsService implements UserDetailsService {

    @Autowired
    UserCredentialsRepository userCredentialsRepository;

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserCredentials credentials = userCredentialsRepository.findByEmail(email);

        if (credentials == null) {
            throw new UsernameNotFoundException(email);
        }

        return new User(credentials.getEmail(), credentials.getPassword(), new ArrayList<>());
    }
}
