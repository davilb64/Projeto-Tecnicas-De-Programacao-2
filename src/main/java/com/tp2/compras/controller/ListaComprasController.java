package com.tp2.compras.controller;

import com.tp2.compras.dto.ComparacaoMercadoDTO;
import com.tp2.compras.dto.ItemListaAdicionarDTO;
import com.tp2.compras.dto.ListaComprasCadastroDTO;
import com.tp2.compras.dto.ListaComprasResponseDTO;
import com.tp2.compras.model.ListaCompras;
import com.tp2.compras.service.ComparacaoService;
import com.tp2.compras.service.ListaComprasService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/listas")
@RequiredArgsConstructor
public class ListaComprasController {

    private final ListaComprasService listaService;
    private final ComparacaoService comparacaoService;

    @PostMapping
    public ResponseEntity<String> criarLista(@Valid @RequestBody ListaComprasCadastroDTO dto) {
        try {
            ListaCompras criada = listaService.cadastrarLista(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Lista '" + criada.getNome() + "' criada com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ListaComprasResponseDTO>> buscarListasDoUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(listaService.listarDoUsuario(usuarioId));
    }

    // --- Endpoints para gerenciar ITENS dentro da lista ---

    @PostMapping("/{listaId}/itens")
    public ResponseEntity<?> adicionarItemNaLista(
            @PathVariable Long listaId, @Valid @RequestBody ItemListaAdicionarDTO dto) {
        try {
            ListaComprasResponseDTO listaAtualizada = listaService.adicionarItem(listaId, dto);
            return ResponseEntity.ok(listaAtualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PatchMapping("/itens/{itemId}/check")
    public ResponseEntity<Void> alternarItemComprado(@PathVariable Long itemId) {
        try {
            listaService.alternarStatusComprado(itemId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/itens/{itemId}")
    public ResponseEntity<Void> removerItem(@PathVariable Long itemId) {
        try {
            listaService.removerItem(itemId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{listaId}")
    public ResponseEntity<Void> deletarLista(@PathVariable Long listaId) {
        try {
            listaService.deletarLista(listaId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{listaId}/comparacao")
    public ResponseEntity<List<ComparacaoMercadoDTO>> compararMercados(@PathVariable Long listaId) {
        try {
            List<ComparacaoMercadoDTO> comparacoes = comparacaoService.compararListaNosMercados(listaId);
            return ResponseEntity.ok(comparacoes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}