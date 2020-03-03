package com.plapp.authservice.security;

import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class JWTAuthenticationProperties {

    @Value("${security.signing-key}")
    private String privateKey;

    @Value("${security.algorithm}")
    private SignatureAlgorithm algorithm;

    @Value("${security.expiration}")
    private long expiration;
}
