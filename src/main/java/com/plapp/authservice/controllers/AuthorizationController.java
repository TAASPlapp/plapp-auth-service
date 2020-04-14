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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthorizationController {
    private final Logger logger = LoggerFactory.getLogger(AuthorizationController.class);

    private final AuthorizationService authorizationService;
    private final JWTAuthenticationManager jwtAuthenticationManager;

    @PostMapping("/{userId}/update")
    public ResourceAuthority updateAuthorization(@PathVariable Long userId,
                                                 @RequestParam String urlRegex,
                                                 @RequestParam Long value) {
        logger.info("Adding authority for user " + userId);
        return authorizationService.addResourceAuthorityValue(userId, urlRegex, value);
    }

    @PostMapping("/{userId}/remove")
    public void removeAuthorization(@PathVariable long userId,
                                                   @RequestParam String urlRegex,
                                                   @RequestParam long value) {
        authorizationService.removeResourceAuthorityValue(userId, urlRegex, value);

    }

    @PostMapping("/jwt/fetch")
    public String generateUpdatedJwt(@RequestBody String jwt) throws JsonProcessingException {
        Jws<Claims> claims = jwtAuthenticationManager.decodeJwt(jwt);
        Long userId = Long.valueOf(claims.getBody().getSubject());
        return authorizationService.buildJwtWithAuthorizations(userId);
    }
}
