package com.proyecto.proyectoweb.controller;

import com.proyecto.proyectoweb.dto.LoginRequest;
import com.proyecto.proyectoweb.dto.LoginResponse;
import com.proyecto.proyectoweb.entity.Usuario;
import com.proyecto.proyectoweb.repository.UsuarioRepository;
import com.proyecto.proyectoweb.service.JwtService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Map<String, String> USERS = Map.of(
        "admin",
        "admin123",
        "user",
        "user123"
    );

    private static final Map<String, List<String>> USER_ROLES = Map.of(
        "admin",
        List.of("ROLE_ADMIN", "ROLE_USER"),
        "user",
        List.of("ROLE_USER")
    );

    public AuthController(
        JwtService jwtService,
        UsuarioRepository usuarioRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private boolean matchPassword(String rawPassword, String encodedPassword) {
        if (encodedPassword == null) return false;
        if (
            encodedPassword.startsWith("$2a$") ||
            encodedPassword.startsWith("$2b$") ||
            encodedPassword.startsWith("$2y$")
        ) {
            return passwordEncoder.matches(rawPassword, encodedPassword);
        }
        return rawPassword.equals(encodedPassword);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // 1. Intentar autenticar con usuarios reales en la Base de Datos
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(
            request.username()
        );
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (matchPassword(request.password(), usuario.getContrasena())) {
                String roleName =
                    usuario.getRol() != null
                        ? "ROLE_" + usuario.getRol().name()
                        : "ROLE_SOLO_LECTURA";
                List<String> roles = List.of(roleName);
                String token = jwtService.generateToken(
                    usuario.getCorreo(),
                    roles
                );

                return ResponseEntity.ok(
                    new LoginResponse(
                        token,
                        3600000, // 1 hora de expiración
                        usuario.getCorreo(),
                        usuario.getRol() != null
                            ? usuario.getRol().name()
                            : null,
                        usuario.getId(),
                        usuario.getNombre()
                    )
                );
            } else {
                return ResponseEntity.status(401).body(
                    Map.of("error", "Credenciales inválidas")
                );
            }
        }

        // 2. Fallback para usuarios mock en memoria (compatibilidad)
        String storedPassword = USERS.get(request.username());
        if (
            storedPassword != null && storedPassword.equals(request.password())
        ) {
            List<String> roles = USER_ROLES.getOrDefault(
                request.username(),
                List.of()
            );
            String token = jwtService.generateToken(request.username(), roles);
            return ResponseEntity.ok(new LoginResponse(token, 3600000));
        }

        return ResponseEntity.status(401).body(
            Map.of("error", "Credenciales inválidas")
        );
    }
}
