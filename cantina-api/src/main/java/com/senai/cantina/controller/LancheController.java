package com.senai.cantina.controller;

import com.senai.cantina.dto.LancheRequestDTO;
import com.senai.cantina.dto.LancheResponseDTO;
import com.senai.cantina.dto.LancheResumoDTO;
import com.senai.cantina.service.LancheService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/lanches")
@RequiredArgsConstructor
public class LancheController {

    private final LancheService lancheService;

    @PostMapping
    public ResponseEntity<LancheResponseDTO> cadastrar(@Valid @RequestBody LancheRequestDTO dto) {
        LancheResponseDTO criado = lancheService.cadastrar(dto);
        URI location = URI.create("/api/lanches/" + criado.getId());
        return ResponseEntity.created(location).body(criado);
    }

    @GetMapping
    public ResponseEntity<List<LancheResumoDTO>> listarTodos() {
        return ResponseEntity.ok(lancheService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LancheResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(lancheService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LancheResponseDTO> atualizar(@PathVariable Long id,
                                                         @Valid @RequestBody LancheRequestDTO dto) {
        return ResponseEntity.ok(lancheService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        lancheService.remover(id);
        return ResponseEntity.noContent().build();
    }
    
}
