package com.plapp.authservice.security;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.plapp.authservice.entity.UserCredentials;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

@Component
@EnableConfigurationProperties(JWTAuthenticationProperties.class)
public class JWTAuthenticationManager {

    private JWTAuthenticationProperties properties;

    @Autowired
    public JWTAuthenticationManager(JWTAuthenticationProperties properties) {
        this.properties = properties;
    }

    public String buildJWT(UserCredentials credentials) {
        long currentMillis = System.currentTimeMillis();
        Date now = new Date(currentMillis);

        byte[] signingKeyBytes = DatatypeConverter.parseBase64Binary(properties.getSigningKey());
        Key signingKey = new SecretKeySpec(signingKeyBytes,
                                           properties.getAlgorithm().getJcaName());

        JwtBuilder jwtBuilder = Jwts.builder().setId(String.valueOf(credentials.getId()))
                                              .setIssuedAt(now)
                                              .setSubject(credentials.getEmail())
                                              .signWith(properties.getAlgorithm(), signingKey);

        if (properties.getExpiration() > 0) {
            long expirationMillis = currentMillis + properties.getExpiration();
            jwtBuilder.setExpiration(new Date(expirationMillis));
        }

        return jwtBuilder.compact();
    }
}
