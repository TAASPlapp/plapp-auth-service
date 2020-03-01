package com.plapp.authservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plapp.authservice.entities.ResourceAuthority;
import com.plapp.authservice.entities.UserCredentialsDPO;
import com.plapp.authservice.repositories.UserCredentialsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest
class AuthServiceApplicationTests {

    @Autowired
    private UserCredentialsRepository userCredentialsRepository;

    @Test
    void contextLoads() {

    }

    @Test
    void testAuthorities() throws Exception {
        UserCredentialsDPO userCredentialsDPO = new UserCredentialsDPO();
        userCredentialsDPO.setEmail("auth@email");
        userCredentialsDPO.setPassword("password");
        ResourceAuthority authority = new ResourceAuthority();
        authority.setAuthority("/auth/([0-9]+)/update");
        //ResourceAuthorityValue value = new ResourceAuthorityValue();
        //value.setResourceValue(1234L);
        authority.setValues(Arrays.asList(1234L));
        userCredentialsDPO.setAuthorities(Arrays.asList(authority));

        UserCredentialsDPO saved1 = userCredentialsRepository.save(userCredentialsDPO);

        userCredentialsDPO = new UserCredentialsDPO();
        userCredentialsDPO.setEmail("auth@email");
        userCredentialsDPO.setPassword("password");
        authority = new ResourceAuthority();
        authority.setAuthority("/auth/([0-9]+)/update");
        userCredentialsDPO.setEmail("auth2@email");
        authority.setValues(Arrays.asList(1235L, 1236L));
        userCredentialsDPO.setAuthorities(Arrays.asList(authority));
        UserCredentialsDPO saved2 = userCredentialsRepository.save(userCredentialsDPO);

        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(saved1));
        System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(saved2));
    }
}
