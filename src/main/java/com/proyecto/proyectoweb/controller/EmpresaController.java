package com.proyecto.proyectoweb.controller;

import java.util.List;
import com.proyecto.proyectoweb.dto.EmpresaDTO;
import com.proyecto.proyectoweb.service.EmpresaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/empresa")
@CrossOrigin(origins = "*")
public class EmpresaController {

    @Autowired
    private EmpresaService empresaService;

    
    @PostMapping("/registrar")
    public ResponseEntity<EmpresaDTO> registrar(@RequestBody EmpresaDTO empresaDTO) {
        try {
            EmpresaDTO resultado = empresaService.registrarEmpresa(empresaDTO);
            return new ResponseEntity<>(resultado, HttpStatus.CREATED);
        } catch (Exception e) {
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<List<EmpresaDTO>> listar() {
        return new ResponseEntity<>(empresaService.listarEmpresas(), HttpStatus.OK);
    }
}