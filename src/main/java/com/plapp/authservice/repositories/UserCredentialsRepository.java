package com.plapp.authservice.repositories;

import com.plapp.entities.auth.UserCredentials;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCredentialsRepository extends JpaRepository<UserCredentials, Integer> {
    UserCredentials findByEmail(String email);
}
