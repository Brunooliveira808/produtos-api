package com.bruno.sistemaProdutos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
                .csrf(AbstractHttpConfigurer::disable)//desabilitar proteção contra CSRF, pois estamos usando JWT e não sessões
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                    .authenticationEntryPoint((new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                    .accessDeniedHandler((request, response, accessDeniedException) -> {
                        response.sendError(HttpStatus.FORBIDDEN.value(), "Acesso negado");
                    })
                )

                .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/auth/**").permitAll()
                    .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/produto").permitAll()
                    .requestMatchers(HttpMethod.GET, "/categoria").permitAll()
                    .requestMatchers(HttpMethod.POST, "/categoria").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/produto").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/produto").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/produto").hasRole("ADMIN")
                        .anyRequest().authenticated()
                ) //permitir acesso às rotas
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) {
        return authenticationConfiguration.getAuthenticationManager();
    }

    
}
