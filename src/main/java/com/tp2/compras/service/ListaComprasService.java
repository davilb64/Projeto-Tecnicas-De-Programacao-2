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

    /**
     * Cria uma nova lista de compras vazia para o usuário.
     *
     * <p>Rastreamento de requisitos:
     * <ul>
     * <li>EU008 - Eu como usuário quero poder criar listas de compras.</li>
     * </ul>
     *
     * <p><b>Argumentação da corretude:</b>
     * Valida a existência do usuário e gera uma lista associada à sua PK.
     */
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

    /**
     * Adiciona um item (variação de produto) dentro de uma lista existente.
     *
     * <p><b>Argumentação da corretude:</b>
     * O repositório verifica duplicatas compostas por lista_id e variacao_id[cite: 17].
     * Se o item já estiver na lista, lança exceção para não ferir a constraint do banco.
     */
    @Transactional
    public ListaComprasResponseDTO adicionarItem(Long listaId, ItemListaAdicionarDTO dto) {
        Assert.notNull(dto, "O DTO do item não pode ser nulo");

        ListaCompras lista = listaRepository.findById(listaId)
                .orElseThrow(() -> new IllegalArgumentException("Lista não encontrada."));

        VariacaoProduto variacao = variacaoRepository.findById(dto.variacaoId())
                .orElseThrow(() -> new IllegalArgumentException("Variação do produto não encontrada."));

        if (itemRepository.existsByListaIdAndVariacaoId(listaId, variacao.getId())) {
            throw new IllegalArgumentException("Este produto já está na sua lista.");
        }

        ItemLista novoItem = ItemLista.builder()
                .lista(lista)
                .variacao(variacao)
                .quantidade(dto.quantidade())
                .comprado(false) // Padrão conforme a Entidade[cite: 20]
                .build();

        lista.getItens().add(novoItem);
        return ListaComprasResponseDTO.daEntidade(listaRepository.save(lista));
    }

    /**
     * Alterna o status de "comprado" do item na hora que o usuário está no mercado.
     */
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