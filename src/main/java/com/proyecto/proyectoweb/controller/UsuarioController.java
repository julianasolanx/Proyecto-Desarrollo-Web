package com.proyecto.proyectoweb.controller;

import com.proyecto.proyectoweb.dto.LoginDTO;
import com.proyecto.proyectoweb.dto.UsuarioDTO;
import com.proyecto.proyectoweb.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuario")
@CrossOrigin(origins = "*") 
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

  
    @PostMapping("/registrar")
    public ResponseEntity<UsuarioDTO> registrarUsuario(@RequestBody UsuarioDTO usuarioDTO) {
        try {
            UsuarioDTO creado = usuarioService.registrarUsuarioEnEmpresa(usuarioDTO);
            return new ResponseEntity<>(creado, HttpStatus.CREATED);
        } catch (Exception e) {
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    
    @PostMapping("/login")
    public ResponseEntity<UsuarioDTO> login(@RequestBody LoginDTO loginDTO) {
        try {
            UsuarioDTO usuarioAutenticado = usuarioService.login(loginDTO);
            return new ResponseEntity<>(usuarioAutenticado, HttpStatus.OK);
        } catch (Exception e) {
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}