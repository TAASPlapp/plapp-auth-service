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
    public void removeAuthorization(@PathVariable long userId,
                                    @RequestParam String urlRegex,
                                    @RequestParam long value) {
        authorizationService.removeResourceAuthorityValue(userId, urlRegex, value);

    }

    @GetMapping("/{userId}/authorities")
    public List<ResourceAuthority> getAuthorities(@PathVariable long userId) {
        return authorizationService.getAuthorities(userId);
    }

    @PostMapping("/update")
    public ResourceAuthority updateAuthority(@RequestParam String urlRegex, @RequestParam Long value) {
        System.out.println("PRINCIPAL: " + SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        Long userId = (Long)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return authorizationService.addResourceAuthorityValue(userId, urlRegex, value);
    }

    @PostMapping("/jwt/fetch")
    public String generateUpdatedJwt(@RequestBody String jwt) throws JsonProcessingException {
        Jws<Claims> claims = jwtAuthenticationManager.decodeJwt(jwt);
        Long userId = Long.valueOf(claims.getBody().getSubject());
        return authorizationService.buildJwtWithAuthorizations(userId);
    }
}
