package com.tp2.compras.controller;

import com.tp2.compras.dto.VariacaoProdutoCadastroDTO;
import com.tp2.compras.dto.VariacaoProdutoResponseDTO;
import com.tp2.compras.dto.VariacaoProdutoUpdateDTO;
import com.tp2.compras.model.VariacaoProduto;
import com.tp2.compras.service.VariacaoProdutoService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Classe controladora de Variações de Produtos.
 * Gera endpoints para o front-end.
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/variacoes")
@RequiredArgsConstructor
public class VariacaoProdutoController {

  private final VariacaoProdutoService variacaoService;

  /**
   * Endpoint de cadastro de variação de produto (POST).
   */
  @PostMapping("/cadastro")
    public ResponseEntity<String> cadastrar(@Valid @RequestBody VariacaoProdutoCadastroDTO dto) {
    try {
      VariacaoProduto criada = variacaoService.cadastrar(dto);
      return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Variação " + criada.getDescricao() + " adicionada com sucesso!");
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
  }

  /**
   * Endpoint de listagem de variações de um produto (GET).
   */
  @GetMapping("/produto/{produtoId}")
    public ResponseEntity<List<VariacaoProdutoResponseDTO>>
      listarPorProduto(@PathVariable Long produtoId) {
    return ResponseEntity.ok(variacaoService.listarPorProduto(produtoId));
  }

  /**
   * Endpoint de atualização de variação de um produto (PUT).
   */
  @PutMapping("/{id}")
    public ResponseEntity<VariacaoProdutoResponseDTO> atualizar(
            @PathVariable Long id, @Valid @RequestBody VariacaoProdutoUpdateDTO dto) {
    try {
      return ResponseEntity.ok(variacaoService.atualizar(id, dto));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Endpoint de remoção de variação de um produto (DELETE).
   */
  @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
    try {
      variacaoService.deletar(id);
      return ResponseEntity.noContent().build();
    } catch (IllegalArgumentException e) {
      return ResponseEntity.notFound().build();
    }
  }
}