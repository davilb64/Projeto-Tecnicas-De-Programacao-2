package com.tp2.compras.service;

import com.tp2.compras.dto.ItemListaAdicionarDTO;
import com.tp2.compras.dto.ListaComprasCadastroDTO;
import com.tp2.compras.dto.ListaComprasResponseDTO;
import com.tp2.compras.model.ItemLista;
import com.tp2.compras.model.ListaCompras;
import com.tp2.compras.model.Usuario;
import com.tp2.compras.model.VariacaoProduto;
import com.tp2.compras.repository.ItemListaRepository;
import com.tp2.compras.repository.ListaComprasRepository;
import com.tp2.compras.repository.UsuarioRepository;
import com.tp2.compras.repository.VariacaoProdutoRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * Métodos de Listas de Compras.
 **/
@Service
@RequiredArgsConstructor
public class ListaComprasService {

  private final ListaComprasRepository listaRepository;
  private final ItemListaRepository itemRepository;
  private final UsuarioRepository usuarioRepository;
  private final VariacaoProdutoRepository variacaoRepository;

  /**
   * Cadastro de listas.
   **/
  @Transactional
    public ListaCompras cadastrarLista(ListaComprasCadastroDTO dto) {
    Assert.notNull(dto, "O DTO da lista não pode ser nulo");

    Usuario dono = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

    ListaCompras novaLista = ListaCompras.builder()
                .usuario(dono)
                .nome(dto.nome())
                .build();

    return listaRepository.save(novaLista);
  }

  /**
   * Lista listas de compras do usuário por ID.
   **/
  public List<ListaComprasResponseDTO> listarDoUsuario(Long usuarioId) {
    return listaRepository.findByUsuarioId(usuarioId).stream()
                .map(ListaComprasResponseDTO::daEntidade)
                .collect(Collectors.toList());
  }

  /**
   * Adiciona item à lista por ID.
   **/
  @Transactional
    public ListaComprasResponseDTO adicionarItem(Long listaId, ItemListaAdicionarDTO dto) {
    ListaCompras lista = listaRepository.findById(listaId).orElseThrow();
    VariacaoProduto variacao = variacaoRepository.findById(dto.variacaoId()).orElseThrow();

    if (itemRepository.existsByListaIdAndVariacaoId(listaId, dto.variacaoId())) {
      throw new IllegalArgumentException("Este produto já está na sua lista.");
    }

    ItemLista novoItem = ItemLista.builder()
                .lista(lista)
                .variacao(variacao)
                .quantidade(dto.quantidade())
                .comprado(false)
                .build();
    lista.getItens().add(novoItem);
    return ListaComprasResponseDTO.daEntidade(listaRepository.save(lista));
  }

  /**
   * Alterna Status do item por ID.
   **/
  @Transactional
    public void alternarStatusComprado(Long itemId) {
    ItemLista item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item não encontrado."));
    item.setComprado(!item.getComprado());
    itemRepository.save(item);
  }

  /**
   * Remove item por ID.
   **/
  @Transactional
    public void removerItem(Long itemId) {
    if (!itemRepository.existsById(itemId)) {
      throw new IllegalArgumentException("Item não encontrado.");
    }
    itemRepository.deleteById(itemId);
  }

  /**
   * Deleta lista por ID.
   **/
  @Transactional
    public void deletarLista(Long listaId) {
    if (!listaRepository.existsById(listaId)) {
      throw new IllegalArgumentException("Lista não encontrada.");
    }
    listaRepository.deleteById(listaId);
  }
}