package com.plapp.authservice.repositories;

import com.plapp.authservice.entity.UserCredentials;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCredentialsRepository extends JpaRepository<UserCredentials, Integer> {

}
