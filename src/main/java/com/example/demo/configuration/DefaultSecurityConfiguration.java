package com.example.demo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;

@Configuration
@EnableWebFluxSecurity
public class DefaultSecurityConfiguration {
    @Bean
    SecurityWebFilterChain defaultSecurityFilterChain(ServerHttpSecurity http) {
        http.cors(corsSpec -> corsSpec.configurationSource(exchange -> {
            CorsConfiguration corsConfiguration = new CorsConfiguration();

            // Define allowed HTTP methods (e.g., GET, POST, PUT, DELETE)
            corsConfiguration.addAllowedMethod("GET");
            corsConfiguration.addAllowedMethod("POST");
            corsConfiguration.addAllowedMethod("PUT");
            corsConfiguration.addAllowedMethod("DELETE");

            // Define allowed headers (e.g., Authorization, Content-Type)
            corsConfiguration.addAllowedHeader("*");
            corsConfiguration.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));

            // Allow credentials if necessary
            corsConfiguration.setAllowCredentials(true);

            return corsConfiguration;
        }));

        http.csrf(ServerHttpSecurity.CsrfSpec::disable);
        http.httpBasic(Customizer.withDefaults());
        return http.build();
    }
}
