package com.plapp.authservice.repositories;

import com.plapp.authservice.entities.ResourceAuthority;
import com.plapp.authservice.entities.UserCredentialsDPO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserCredentialsRepository extends JpaRepository<UserCredentialsDPO, Integer> {
    UserCredentialsDPO findByEmail(String email);
}
