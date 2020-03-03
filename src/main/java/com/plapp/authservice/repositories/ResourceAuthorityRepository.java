package com.plapp.authservice.repositories;

import com.plapp.authorization.ResourceAuthority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResourceAuthorityRepository extends JpaRepository<ResourceAuthority, Integer> {
    List<ResourceAuthority> findAllByAuthority(String authority);
    List<ResourceAuthority> findAllByUserId(long userId);
    ResourceAuthority findByUserIdAndAuthority(long userId, String authority);
}
