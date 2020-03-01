package com.plapp.authservice.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plapp.authservice.entities.ResourceAuthority;
import com.plapp.entities.auth.UserCredentials;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.*;

@Component
@RequiredArgsConstructor
public class JWTAuthenticationManager {
    private final JWTAuthenticationProperties properties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String buildJWT(UserCredentials credentials, Collection<? extends GrantedAuthority> authorities) throws JsonProcessingException  {
        long currentMillis = System.currentTimeMillis();
        Date now = new Date(currentMillis);

        byte[] signingKeyBytes = DatatypeConverter.parseBase64Binary(properties.getSigningKey());
        Key signingKey = new SecretKeySpec(signingKeyBytes,
                                           properties.getAlgorithm().getJcaName());

        String authoritiesString = objectMapper.writeValueAsString(authorities);

        JwtBuilder jwtBuilder = Jwts.builder().setId(String.valueOf(credentials.getId()))
                                              .setIssuedAt(now)
                                              .setSubject(String.valueOf(credentials.getId()))
                                              .claim("authorities", authoritiesString)
                                              .signWith(signingKey);

        if (properties.getExpiration() > 0) {
            long expirationMillis = currentMillis + properties.getExpiration();
            jwtBuilder.setExpiration(new Date(expirationMillis));
        }

        return jwtBuilder.compact();
    }

    public void verifyJwt(String jwt) throws JwtException {
        Jwts.parser()
           .setSigningKey(DatatypeConverter.parseBase64Binary(properties.getSigningKey()))
           .parseClaimsJws(jwt);
    }
}
