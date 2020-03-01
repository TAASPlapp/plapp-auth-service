package com.plapp.authservice.services;

import com.plapp.authservice.entities.ResourceAuthority;
import com.plapp.authservice.repositories.ResourceAuthorityRepository;
import com.plapp.authservice.repositories.UserCredentialsRepository;
import com.plapp.entities.auth.UserCredentials;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SpringUserService implements UserDetailsService {
    private final UserCredentialsRepository userCredentialsRepository;
    private final ResourceAuthorityRepository resourceAuthorityRepository;

    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        UserCredentials credentials = userCredentialsRepository.findByEmail(email);

        if (credentials == null) {
            throw new UsernameNotFoundException(email);
        }

        List<ResourceAuthority> authorities = resourceAuthorityRepository.findAllByUserId(credentials.getId());
        return new User(credentials.getEmail(), credentials.getPassword(), authorities);
    }
}
