package com.proyecto.proyectoweb.controller;
import com.proyecto.proyectoweb.dto.CrearUsuarioDTO;
import com.proyecto.proyectoweb.dto.EmailRequestDTO;
import com.proyecto.proyectoweb.dto.LoginRequestDTO;
import com.proyecto.proyectoweb.dto.UsuarioDTO;
import com.proyecto.proyectoweb.service.EmailService;
import com.proyecto.proyectoweb.service.UsuarioService;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioService usuarioService;
    private final EmailService emailService;

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listar() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<UsuarioDTO>> listarPorEmpresa(@PathVariable Long empresaId) {
        return ResponseEntity.ok(usuarioService.listarPorEmpresa(empresaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerUsuario(id));
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO> crear(@RequestBody CrearUsuarioDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crearUsuario(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioDTO> login(@RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(usuarioService.login(dto.getCorreo(), dto.getContrasena()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> actualizar(@PathVariable Long id, @RequestBody UsuarioDTO dto) {
        return ResponseEntity.ok(usuarioService.actualizarUsuario(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/enviar-invitacion")
    public ResponseEntity<String> enviarInvitacion(@RequestBody EmailRequestDTO dto) {
        emailService.enviarCorreo(dto.getTo(), dto.getSubject(), dto.getMessage());
        return ResponseEntity.ok("Correo enviado correctamente");
    }

}
