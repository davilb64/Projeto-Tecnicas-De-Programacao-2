package com.tp2.compras.controller;

import com.tp2.compras.dto.EstimativaPrecoDTO;
import com.tp2.compras.dto.PrecoCadastroDTO;
import com.tp2.compras.dto.PrecoResponseDTO;
import com.tp2.compras.dto.PrecoUpdateDTO;
import com.tp2.compras.service.PrecoService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints para controle de preços.
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/precos")
@RequiredArgsConstructor
public class PrecoController {

  private final PrecoService precoService;

  /**
   * Endpoint para registro de preço (POST).
   * Recebe os dados de entrada, valida os formatos e repassa para a camada de serviço.
   *
   * @param dto objeto envelopado contendo variação, estabelecimento,
   *            usuário e valor enviados no corpo da requisição (POST).
   * @return ResponseEntity contendo a mensagem de sucesso e status HTTP 201 (Created).
   */
  @PostMapping
    public ResponseEntity<PrecoResponseDTO> registrarPreco(@Valid @RequestBody
                                                             PrecoCadastroDTO dto) {
    try {
      PrecoResponseDTO response = precoService.registrarPreco(dto);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  /**
   * Endpoint para busca de preço (GET).
   * Recebe os dados de entrada, valida os formatos e repassa para a camada de serviço.
   *
   * @param id id do preço desejado.
   * @return ResponseEntity contendo a mensagem de sucesso e status HTTP 201 (Created).
   */
  @GetMapping("/{id}")
    public ResponseEntity<PrecoResponseDTO> buscarPorId(@PathVariable Long id) {
    try {
      return ResponseEntity.ok(precoService.buscarPorId(id));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Endpoint para listar todos os preços no repository (GET).
   *
   * @return ResponseEntity contendo a mensagem de sucesso e status HTTP 201 (Created).
   */
  @GetMapping
    public ResponseEntity<List<PrecoResponseDTO>> listarTodos() {
    return ResponseEntity.ok(precoService.listarTodos());
  }

  /**
   * Endpoint para buscar histórico de preço de uma variação de produto específica (GET).
   * Recebe os dados de entrada, valida os formatos e repassa para a camada de serviço.
   *
   * @param variacaoId id da variacao do produto desejada.
   * @param estabelecimentoId id do estabelecimento consultado.
   * @return ResponseEntity contendo a mensagem de sucesso e status HTTP 201 (Created).
   */
  @GetMapping("/historico")
    public ResponseEntity<List<PrecoResponseDTO>> buscarHistorico(
            @RequestParam Long variacaoId,
            @RequestParam Long estabelecimentoId) {
    return ResponseEntity.ok(precoService.buscarHistorico(variacaoId, estabelecimentoId));
  }

  /**
   * Endpoint para atualização de preços (PUT).
   **/
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

  /**
   * Endpoint para deletar preços (DELETE).
   **/
  @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
    try {
      precoService.deletar(id);
      return ResponseEntity.noContent().build();
    } catch (IllegalArgumentException e) {
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Endpoint para estimar preços (GET).
   **/
  @GetMapping("/estimativa/{variacaoId}")
    public ResponseEntity<EstimativaPrecoDTO> buscarEstimativa(
            @PathVariable Long variacaoId,
            @RequestParam Long estabelecimentoId) {
    return ResponseEntity.ok(precoService.obterEstimativaItem(variacaoId, estabelecimentoId));
  }
}