package com.tp2.compras.controller;

import com.tp2.compras.dto.ProdutoCadastroDTO;
import com.tp2.compras.dto.ProdutoResponseDTO;
import com.tp2.compras.dto.ProdutoUpdateDTO;
import com.tp2.compras.model.Produto;
import com.tp2.compras.service.ProdutoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;

    /**
     * Endpoint para cadastro de novos produtos (EU004).
     *
     * <p><b>Assertiva de entrada:</b> dto não nulo e validado conforme restrições de ProdutoCadastroDTO.
     * <p><b>Assertiva de saída:</b> retorna o nome do produto e status HTTP 201 (Created).
     *
     * <p><b>Argumentação da corretude:</b>
     * O Spring intercepta a requisição, valida o @Valid e repassa ao service. Se houver falha
     * de negócio (código de barras duplicado), uma exceção sobe e impede o HTTP 201.
     */
    @PostMapping("/cadastro")
    public ResponseEntity<String> cadastrar(@Valid @RequestBody ProdutoCadastroDTO dto) {
        try {
            Produto produtoCriado = produtoService.cadastrar(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Produto " + produtoCriado.getNome() + " cadastrado com sucesso!");
        } catch (IllegalArgumentException e) {
            // Em caso de código de barras duplicado (EU006)
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarProduto(@PathVariable Long id) {
        try {
            ProdutoResponseDTO response = produtoService.buscarPorId(id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ProdutoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(produtoService.listarTodos());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> atualizarProduto(
            @PathVariable Long id,
            @Valid @RequestBody ProdutoUpdateDTO dto) {
        try {
            ProdutoResponseDTO response = produtoService.atualizar(id, dto);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint para o Administrador aprovar ou rejeitar um produto pendente.
     *
     * <p><b>Assertiva de entrada:</b> id do produto não nulo e string de status válida (APROVADO, REJEITADO).
     * <p><b>Assertiva de saída:</b> retorna status HTTP 200 (OK) se a atualização for bem-sucedida.
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<String> alterarStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        try {
            produtoService.alterarStatus(id, status);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            // Em caso de produto não encontrado ou status inválido
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProduto(@PathVariable Long id) {
        try {
            produtoService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}