package com.plapp.authservice.security;

import com.plapp.entities.auth.UserCredentials;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

@Component
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

    public void verifyJwt(String jwt) throws JwtException {
        Jwts.parser()
           .setSigningKey(DatatypeConverter.parseBase64Binary(properties.getSigningKey()))
           .parseClaimsJws(jwt);
    }
}
