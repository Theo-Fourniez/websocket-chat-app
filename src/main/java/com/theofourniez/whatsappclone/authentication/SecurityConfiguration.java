package com.theofourniez.whatsappclone.authentication;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@EnableWebFluxSecurity
@Configuration
public class SecurityConfiguration implements WebFluxConfigurer {
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Use NoOpPasswordEncoder for clear text encoding (NOT RECOMMENDED FOR PRODUCTION)
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        UserDetails user = User.builder()
                .username("testman")
                .password("testman")
                .roles("USER")
                .build();
        UserDetails user2 = User.builder()
                .username("user")
                .password("test")
                .roles("USER")
                .build();
        return new MapReactiveUserDetailsService(user, user2);
    }

}
