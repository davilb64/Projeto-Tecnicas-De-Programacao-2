package com.tp2.compras.controller;

import com.tp2.compras.dto.EstimativaPrecoDTO;
import com.tp2.compras.dto.PrecoCadastroDTO;
import com.tp2.compras.dto.PrecoResponseDTO;
import com.tp2.compras.dto.PrecoUpdateDTO;
import com.tp2.compras.service.PrecoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/precos")
@RequiredArgsConstructor
public class PrecoController {

    private final PrecoService precoService;

    @PostMapping
    public ResponseEntity<PrecoResponseDTO> registrarPreco(@Valid @RequestBody PrecoCadastroDTO dto) {
        try {
            PrecoResponseDTO response = precoService.registrarPreco(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrecoResponseDTO> buscarPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(precoService.buscarPorId(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<PrecoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(precoService.listarTodos());
    }

    // Exemplo de uso: GET /api/precos/historico?variacaoId=1&estabelecimentoId=2
    @GetMapping("/historico")
    public ResponseEntity<List<PrecoResponseDTO>> buscarHistorico(
            @RequestParam Long variacaoId,
            @RequestParam Long estabelecimentoId) {
        return ResponseEntity.ok(precoService.buscarHistorico(variacaoId, estabelecimentoId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PrecoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody PrecoUpdateDTO dto) {
        try {
            return ResponseEntity.ok(precoService.atualizar(id, dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            precoService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/estimativa/{variacaoId}")
    public ResponseEntity<EstimativaPrecoDTO> buscarEstimativa(
            @PathVariable Long variacaoId,
            @RequestParam Long estabelecimentoId) {
        return ResponseEntity.ok(precoService.obterEstimativaItem(variacaoId, estabelecimentoId));
    }
}