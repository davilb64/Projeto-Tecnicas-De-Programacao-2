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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListaComprasService {

    private final ListaComprasRepository listaRepository;
    private final ItemListaRepository itemRepository;
    private final UsuarioRepository usuarioRepository;
    private final VariacaoProdutoRepository variacaoRepository;

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

    public List<ListaComprasResponseDTO> listarDoUsuario(Long usuarioId) {
        return listaRepository.findByUsuarioId(usuarioId).stream()
                .map(ListaComprasResponseDTO::daEntidade)
                .collect(Collectors.toList());
    }

    @Transactional
    public ListaComprasResponseDTO adicionarItem(Long listaId, ItemListaAdicionarDTO dto) {
        ListaCompras lista = listaRepository.findById(listaId).orElseThrow();
        VariacaoProduto variacao = variacaoRepository.findById(dto.variacaoId()).orElseThrow();

        ItemLista itemExistente = lista.getItens().stream()
                .filter(i -> i.getVariacao().getId().equals(variacao.getId()))
                .findFirst().orElse(null);

        if (itemExistente != null) {
            itemExistente.setQuantidade(itemExistente.getQuantidade() + dto.quantidade());
        } else {
            ItemLista novoItem = ItemLista.builder()
                    .lista(lista)
                    .variacao(variacao)
                    .quantidade(dto.quantidade())
                    .comprado(false)
                    .build();
            lista.getItens().add(novoItem);
        }
        return ListaComprasResponseDTO.daEntidade(listaRepository.save(lista));
    }

    @Transactional
    public void alternarStatusComprado(Long itemId) {
        ItemLista item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item não encontrado."));
        item.setComprado(!item.getComprado());
        itemRepository.save(item);
    }

    @Transactional
    public void removerItem(Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new IllegalArgumentException("Item não encontrado.");
        }
        itemRepository.deleteById(itemId);
    }

    @Transactional
    public void deletarLista(Long listaId) {
        if (!listaRepository.existsById(listaId)) {
            throw new IllegalArgumentException("Lista não encontrada.");
        }
        listaRepository.deleteById(listaId);
    }
}