package com.plapp.authservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.plapp.authservice.entities.ResourceAuthority;
import com.plapp.authservice.repositories.ResourceAuthorityRepository;
import com.plapp.authservice.security.JWTAuthenticationManager;
import com.plapp.entities.auth.UserCredentials;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorizationService {
    private final ResourceAuthorityRepository resourceAuthorityRepository;
    private final JWTAuthenticationManager jwtAuthenticationManager;

    public ResourceAuthority saveResourceAuthority(ResourceAuthority resourceAuthority) {
        return resourceAuthorityRepository.save(resourceAuthority);
    }

    public ResourceAuthority addResourceAuthority(Long userId, String urlRegex, List<Long> values) {
        ResourceAuthority resourceAuthority = new ResourceAuthority();
        resourceAuthority.setAuthority(urlRegex);
        resourceAuthority.setUserId(userId);
        values.forEach(resourceAuthority::addValue);

        return saveResourceAuthority(resourceAuthority);
    }

    public ResourceAuthority addResourceAuthorityValue(Long userId, String urlRegex, Long value) {
        ResourceAuthority resourceAuthority = resourceAuthorityRepository
                .findByUserIdAndAuthority(userId, urlRegex);

        if (resourceAuthority == null)
            resourceAuthority = new ResourceAuthority(urlRegex, userId);

        resourceAuthority.addValue(value);

        return saveResourceAuthority(resourceAuthority);
    }

    public ResourceAuthority removeResourceAuthorityValue(Long userId, String urlRegex, Long value) {
        ResourceAuthority resourceAuthority = resourceAuthorityRepository
                .findByUserIdAndAuthority(userId, urlRegex);
        if (resourceAuthority == null)
            return null;

        resourceAuthority.getValues().remove(value);

        return saveResourceAuthority(resourceAuthority);
    }

    public String buildJwtWithAuthorizations(Long userId) throws JsonProcessingException {
        List<ResourceAuthority> authorities = resourceAuthorityRepository.findAllByUserId(userId);
        return jwtAuthenticationManager.buildJWT(userId, authorities);
    }
}
