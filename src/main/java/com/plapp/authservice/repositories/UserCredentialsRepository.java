package com.plapp.authservice.repositories;

import com.plapp.authservice.entities.ResourceAuthority;
import com.plapp.entities.auth.UserCredentials;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserCredentialsRepository extends JpaRepository<UserCredentials, Integer> {
    UserCredentials findByEmail(String email);
}
