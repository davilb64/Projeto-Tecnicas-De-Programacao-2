package com.tp2.compras.service;

import com.tp2.compras.dto.ItemListaAdicionarDTO;
import com.tp2.compras.dto.ListaComprasResponseDTO;
import com.tp2.compras.model.ListaCompras;
import com.tp2.compras.model.VariacaoProduto;
import com.tp2.compras.repository.ItemListaRepository;
import com.tp2.compras.repository.ListaComprasRepository;
import com.tp2.compras.repository.VariacaoProdutoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListaComprasServiceTest {

    @Mock
    private ListaComprasRepository listaRepository;

    @Mock
    private ItemListaRepository itemRepository;

    @Mock
    private VariacaoProdutoRepository variacaoRepository;

    @InjectMocks
    private ListaComprasService service;

    @Test
    @DisplayName("Deve adicionar um item novo na lista com sucesso")
    void deveAdicionarItemNaLista() {
        // 1. Preparação
        Long listaId = 1L;
        ItemListaAdicionarDTO dto = new ItemListaAdicionarDTO(10L, 2); // ID da variação 10, Qtd 2

        ListaCompras listaMock = ListaCompras.builder().id(listaId).nome("Compras de Sexta").itens(new ArrayList<>()).build();
        VariacaoProduto variacaoMock = VariacaoProduto.builder().id(10L).descricao("Coca Cola 2L").build();

        // Ensinando o Mockito a fingir que achou a lista e o produto no banco
        when(listaRepository.findById(listaId)).thenReturn(Optional.of(listaMock));
        when(variacaoRepository.findById(10L)).thenReturn(Optional.of(variacaoMock));

        // Simula que o item NÃO existe na lista ainda
        when(itemRepository.existsByListaIdAndVariacaoId(listaId, 10L)).thenReturn(false);
        when(listaRepository.save(any(ListaCompras.class))).thenReturn(listaMock);

        // 2. Ação
        ListaComprasResponseDTO resultado = service.adicionarItem(listaId, dto);

        // 3. Garantias
        assertNotNull(resultado);
        verify(listaRepository, times(1)).save(any(ListaCompras.class));
    }

    @Test
    @DisplayName("Deve impedir adição de item repetido na mesma lista")
    void deveImpedirItemRepetido() {
        // 1. Preparação
        Long listaId = 1L;
        ItemListaAdicionarDTO dto = new ItemListaAdicionarDTO(10L, 2);

        ListaCompras listaMock = ListaCompras.builder().id(listaId).build();
        VariacaoProduto variacaoMock = VariacaoProduto.builder().id(10L).build();

        when(listaRepository.findById(listaId)).thenReturn(Optional.of(listaMock));
        when(variacaoRepository.findById(10L)).thenReturn(Optional.of(variacaoMock));

        // Simula que o banco respondeu que o item JÁ EXISTE na lista
        when(itemRepository.existsByListaIdAndVariacaoId(listaId, 10L)).thenReturn(true);

        // 2 e 3. Ação e Garantias
        IllegalArgumentException erro = assertThrows(IllegalArgumentException.class, () -> {
            service.adicionarItem(listaId, dto);
        });

        assertEquals("Este produto já está na sua lista.", erro.getMessage());
        verify(listaRepository, never()).save(any(ListaCompras.class));
    }
}