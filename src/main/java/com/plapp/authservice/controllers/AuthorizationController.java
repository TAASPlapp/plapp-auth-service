package com.plapp.authservice.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.plapp.authorization.ResourceAuthority;
import com.plapp.authservice.security.JWTAuthenticationManager;
import com.plapp.authservice.services.AuthorizationService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthorizationController {
    private final Logger logger = LoggerFactory.getLogger(AuthorizationController.class);

    private final AuthorizationService authorizationService;
    private final JWTAuthenticationManager jwtAuthenticationManager;

    @PostMapping("/{userId}/add")
    public ResourceAuthority addAuthority(@PathVariable Long userId,
                                          @RequestBody ResourceAuthority resourceAuthority) {
        logger.info("Adding authority for user " + userId);
        resourceAuthority.setUserId(userId);
        return authorizationService.addResourceAuthority(resourceAuthority);
    }

    @PostMapping("/{userId}/remove")
    public void removeAuthorization(@PathVariable Long userId,
                                    @RequestParam String urlRegex,
                                    @RequestParam Long value) {
        authorizationService.removeResourceAuthorityValue(userId, urlRegex, value);

    }

    @GetMapping("/{userId}/authorities")
    public List<ResourceAuthority> getAuthorities(@PathVariable Long userId) {
        return authorizationService.getAuthorities(userId);
    }

    @PostMapping("/update")
    public ResourceAuthority updateAuthority(@RequestBody Map<String, Object> params) {
        String urlRegex = (String)params.get("urlRegex");
        Integer value = (Integer)params.get("value");
        System.out.println("PRINCIPAL: " + SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        Long userId = (Long)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return authorizationService.addResourceAuthorityValue(userId, urlRegex, value.longValue());
    }

    @PostMapping("/jwt/fetch")
    public String generateUpdatedJwt(@RequestBody String jwt) throws JsonProcessingException {
        Jws<Claims> claims = jwtAuthenticationManager.decodeJwt(jwt);
        Long userId = Long.valueOf(claims.getBody().getSubject());
        return authorizationService.buildJwtWithAuthorizations(userId);
    }
}
