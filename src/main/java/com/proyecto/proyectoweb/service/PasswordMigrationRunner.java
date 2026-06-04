package com.proyecto.proyectoweb.service;

import com.proyecto.proyectoweb.entity.Usuario;
import com.proyecto.proyectoweb.repository.UsuarioRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PasswordMigrationRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(PasswordMigrationRunner.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordMigrationRunner(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        List<Usuario> usuarios = usuarioRepository.findAll();
        int migrados = 0;

        for (Usuario usuario : usuarios) {
            String contrasena = usuario.getContrasena();
            if (contrasena != null && !esBcrypt(contrasena)) {
                usuario.setContrasena(passwordEncoder.encode(contrasena));
                usuarioRepository.save(usuario);
                migrados++;
            }
        }

        if (migrados > 0) {
            log.info("Migración de contraseñas completada: {} usuario(s) actualizados a BCrypt.", migrados);
        } else {
            log.info("Migración de contraseñas: todas las contraseñas ya están encriptadas.");
        }
    }

    private boolean esBcrypt(String contrasena) {
        return contrasena.startsWith("$2a$")
            || contrasena.startsWith("$2b$")
            || contrasena.startsWith("$2y$");
    }
}
