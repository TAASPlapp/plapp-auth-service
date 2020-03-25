package com.plapp.authservice.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.xml.bind.DatatypeConverter;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

@Component
@RequiredArgsConstructor
public class JWTAuthenticationManager {
    private final JWTAuthenticationProperties properties;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Logger logger = LoggerFactory.getLogger(JWTAuthenticationManager.class);

    private PrivateKey privateKey;

    @PostConstruct
    public void readPrivateKey() throws Exception {
        String url = "https://github.com/TAASPlapp/plapp-auth-service/raw/master/src/main/resources/private.der";

        logger.info("Loading public key file from " + url);
        InputStream inputStream = new URL(url).openStream();
        byte[] keyBytes = new byte[inputStream.available()]; //Files.readAllBytes(Paths.get(path));
        inputStream.read(keyBytes);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        privateKey = keyFactory.generatePrivate(keySpec);
    }

    public String buildJWT(Long userId, Collection<? extends GrantedAuthority> authorities) throws JsonProcessingException  {
        long currentMillis = System.currentTimeMillis();
        Date now = new Date(currentMillis);

        String authoritiesString = objectMapper.writeValueAsString(authorities);
        JwtBuilder jwtBuilder = Jwts.builder().setId(UUID.randomUUID().toString())
                                              .setIssuedAt(now)
                                              .setSubject(String.valueOf(userId))
                                              .claim("authorities", authoritiesString)
                                              .signWith(privateKey);

        if (properties.getExpiration() > 0) {
            long expirationMillis = currentMillis + properties.getExpiration();
            jwtBuilder.setExpiration(new Date(expirationMillis));
        }

        return jwtBuilder.compact();
    }

    public Jws<Claims> decodeJwt(String jwt) throws JwtException {
        return Jwts.parser()
                   .setSigningKey(DatatypeConverter.parseBase64Binary(properties.getPrivateKey()))
                   .parseClaimsJws(jwt);
    }
}
