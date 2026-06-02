package com.proyecto.proyectoweb.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final SecretKey key;
    private final String issuer;
    private final long expirationMs;

    public JwtService(
        @Value("${jwt.secret}") String secret,
        @Value("${jwt.issuer}") String issuer,
        @Value("${jwt.expiration-ms}") long expirationMs
    ) {
        // Convierte el string a una clave HMAC válida
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.issuer = issuer;
        this.expirationMs = expirationMs;
    }

    public String generateToken(String username, List<String> roles) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
            .setSubject(username) // claim "sub"
            .setIssuer(issuer) // claim "iss" → "auth-server"
            .setIssuedAt(now) // claim "iat"
            .setExpiration(expiry) // claim "exp"
            .claim("roles", roles) // claim custom con los roles
            .signWith(key) // firma HMAC-SHA256
            .compact(); // → "eyJhbG..."
    }
}
