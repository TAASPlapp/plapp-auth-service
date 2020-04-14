package com.plapp.authservice.security;

import com.plapp.authorization.JWTAuthorizationRegexFilter;
import com.plapp.authservice.services.SpringUserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@Order(1)
@RequiredArgsConstructor
@Getter
@Setter
public class AuthenticationConfig extends WebSecurityConfigurerAdapter {
    @Value("${security.public-key}")
    private String publicKey;

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
                             .antMatchers("/auth/*").permitAll()
                             .anyRequest().authenticated()
                             .and().addFilter(new JWTAuthorizationRegexFilter(authenticationManager(), this.publicKey));
    }
}
