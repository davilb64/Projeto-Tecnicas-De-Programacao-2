package com.tp2.compras.controller;

import com.tp2.compras.dto.EstabelecimentoCadastroDTO;
import com.tp2.compras.dto.EstabelecimentoResponseDTO;
import com.tp2.compras.dto.EstabelecimentoUpdateDTO;
import com.tp2.compras.model.Estabelecimento;
import com.tp2.compras.service.EstabelecimentoService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Classe controladora de estabelecimentos.
 * Gera endpoints para o front-end.
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/estabelecimentos")
@RequiredArgsConstructor
public class EstabelecimentoController {

  private final EstabelecimentoService estabelecimentoService;

  /**
   * Endpoint para cadastro DIRETO de estabelecimentos (Admin).
   * Responde ao POST na raiz: /api/estabelecimentos
   */
  @PostMapping
    public ResponseEntity<String> cadastrarDireto(@Valid @RequestBody
                                                    EstabelecimentoCadastroDTO dto) {
    return cadastrar(dto);
  }

  /**
   * Endpoint legado para cadastro.
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

  /**
   *  Endpoint para o usuário comum sugerir um novo mercado.
   */
  @PostMapping("/solicitar")
    public ResponseEntity<String> solicitarMercado(@Valid @RequestBody
                                                     EstabelecimentoCadastroDTO dto) {
    try {
      estabelecimentoService.solicitar(dto); // Deve salvar com aprovado = false
      return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Sugestão enviada com sucesso!");
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
  }

  /**
   * Buscar apenas os mercados que aguardam aprovação (Admin).
   */
  @GetMapping("/pendentes")
    public ResponseEntity<List<EstabelecimentoResponseDTO>> buscarPendentes() {
    return ResponseEntity.ok(estabelecimentoService.buscarPendentes());
  }

  /**
   * O Admin aprova a sugestão de um mercado.
   */
  @PatchMapping("/{id}/aprovar")
    public ResponseEntity<Void> aprovarMercado(@PathVariable Long id) {
    try {
      estabelecimentoService.aprovar(id);
      return ResponseEntity.ok().build();
    } catch (IllegalArgumentException e) {
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Endpoint de busca de estabelecimento por ID.
   */
  @GetMapping("/{id}")
    public ResponseEntity<EstabelecimentoResponseDTO> buscar(@PathVariable Long id) {
    try {
      return ResponseEntity.ok(estabelecimentoService.buscarPorId(id));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Lista estabelecimentos.
   */
  @GetMapping
    public ResponseEntity<List<EstabelecimentoResponseDTO>> listar(
            @RequestParam(required = false) String nome) {
    return ResponseEntity.ok(estabelecimentoService.listar(nome));
  }

  /**
   * Atualiza estabelecimento por ID.
   */
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

  /**
   * Deleta estabelecimento por ID.
   */
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