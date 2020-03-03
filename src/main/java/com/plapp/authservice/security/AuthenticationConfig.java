package com.plapp.authservice.security;

import com.plapp.authorization.AuthorizationConfig;
import com.plapp.authorization.JWTAuthorizationRegexFilter;
import com.plapp.authservice.services.SpringUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@Order(1)
@RequiredArgsConstructor
public class AuthenticationConfig extends AuthorizationConfig {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final SpringUserService springUserService;

    @Bean
    public AuthenticationManager getAuthenticationManager() throws Exception {
        return authenticationManager();
    }

    @Override
    public void configure(AuthenticationManagerBuilder builder) throws Exception {
        builder.userDetailsService(springUserService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests()
                             .antMatchers("/auth/login", "/auth/signup")
                             .permitAll()
                             .anyRequest().authenticated()
                             .and().addFilter(new JWTAuthorizationRegexFilter(authenticationManager()));
    }
}
