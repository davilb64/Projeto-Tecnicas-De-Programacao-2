package com.tp2.compras.controller;

import com.tp2.compras.dto.EstabelecimentoCadastroDTO;
import com.tp2.compras.dto.EstabelecimentoResponseDTO;
import com.tp2.compras.dto.EstabelecimentoUpdateDTO;
import com.tp2.compras.model.Estabelecimento;
import com.tp2.compras.service.EstabelecimentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/estabelecimentos")
@RequiredArgsConstructor
public class EstabelecimentoController {

    private final EstabelecimentoService estabelecimentoService;

    /**
     * Endpoint para cadastro de estabelecimentos (EU003).
     */
    @PostMapping("/cadastro")
    public ResponseEntity<String> cadastrar(@Valid @RequestBody EstabelecimentoCadastroDTO dto) {
        try {
            Estabelecimento criado = estabelecimentoService.cadastrar(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Estabelecimento " + criado.getNome() + " cadastrado com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstabelecimentoResponseDTO> buscar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(estabelecimentoService.buscarPorId(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint para listar os estabelecimentos (EU007).
     * Permite o uso de Query Param para filtrar: /api/estabelecimentos?nome=Carrefour
     */
    @GetMapping
    public ResponseEntity<List<EstabelecimentoResponseDTO>> listar(
            @RequestParam(required = false) String nome) {
        return ResponseEntity.ok(estabelecimentoService.listar(nome));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstabelecimentoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody EstabelecimentoUpdateDTO dto) {
        try {
            return ResponseEntity.ok(estabelecimentoService.atualizar(id, dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build(); // 404
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            estabelecimentoService.deletar(id);
            return ResponseEntity.noContent().build(); // 204
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build(); // 404
        }
    }
}