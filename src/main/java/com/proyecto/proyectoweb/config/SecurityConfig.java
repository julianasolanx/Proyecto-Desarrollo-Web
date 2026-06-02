package com.javeriana.auth_server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired; // Importante para inyectar tu filtro
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Asegúrate de importar tu clase JwtAuthenticationFilter (o como se llame en tu proyecto)
import com.proyecto.proyectoweb.config.JwtAuthenticationFilter; 

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 1. Inyectas el filtro de JWT que solucionamos en los pasos anteriores
    @Autowired
    private JwtAuthenticationFilter jwtFilter; 

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                
                .csrf(csrf -> csrf.disable())           
                
               
                .sessionManagement(sm ->
                    sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) 
               
                .authorizeHttpRequests(auth -> auth
                        
                        .requestMatchers("/auth/**").permitAll()  
                        
                        .anyRequest().authenticated()
                )
                
               
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                
                .build();
    }
}