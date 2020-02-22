package com.plapp.authservice.security;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.plapp.entities.auth.UserCredentials;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import javax.xml.crypto.Data;
import java.security.Key;
import java.util.Date;

@Component
//@EnableConfigurationProperties(JWTAuthenticationProperties.class)
public class JWTAuthenticationManager {

    @Autowired
    private JWTAuthenticationProperties properties;

    public String buildJWT(UserCredentials credentials) {
        long currentMillis = System.currentTimeMillis();
        Date now = new Date(currentMillis);

        byte[] signingKeyBytes = DatatypeConverter.parseBase64Binary(properties.getSigningKey());
        Key signingKey = new SecretKeySpec(signingKeyBytes,
                                           properties.getAlgorithm().getJcaName());

        JwtBuilder jwtBuilder = Jwts.builder().setId(String.valueOf(credentials.getId()))
                                              .setIssuedAt(now)
                                              .setSubject(String.valueOf(credentials.getId()))
                                              .signWith(properties.getAlgorithm(), signingKey);

        if (properties.getExpiration() > 0) {
            long expirationMillis = currentMillis + properties.getExpiration();
            jwtBuilder.setExpiration(new Date(expirationMillis));
        }

        return jwtBuilder.compact();
    }

    public Claims verifyJWT(String jwt) {
        Claims claims = Jwts.parser()
                            .setSigningKey(DatatypeConverter.parseBase64Binary(properties.getSigningKey()))
                            .parseClaimsJws(jwt)
                            .getBody();
        return claims;
    }
}
