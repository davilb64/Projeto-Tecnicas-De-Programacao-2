package com.tp2.compras.service;

import com.tp2.compras.dto.EstabelecimentoCadastroDTO;
import com.tp2.compras.model.Estabelecimento;
import com.tp2.compras.repository.EstabelecimentoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstabelecimentoServiceTest {

    @Mock
    private EstabelecimentoRepository repository;

    @InjectMocks
    private EstabelecimentoService service;

    @Test
    @DisplayName("Deve cadastrar um estabelecimento com sucesso")
    void deveCadastrarEstabelecimento() {
        // 1. Prepara os dados falsos (Mock)
        EstabelecimentoCadastroDTO dto = new EstabelecimentoCadastroDTO("Supermercado Extra", "Rua A");

        // Simula que o banco respondeu que NÃO existe mercado com esse nome
        when(repository.existsByNomeIgnoreCase("Supermercado Extra")).thenReturn(false);

        // Simula o salvamento e retorno do ID
        Estabelecimento salvo = Estabelecimento.builder().id(1L).nome("Supermercado Extra").build();
        when(repository.save(any(Estabelecimento.class))).thenReturn(salvo);

        // 2. Executa a ação
        Estabelecimento resultado = service.cadastrar(dto);

        // 3. Verifica as garantias (Assertivas)
        assertNotNull(resultado.getId(), "O ID não deveria ser nulo após salvar");
        assertEquals("Supermercado Extra", resultado.getNome());

        // Garante que o método save() do banco foi chamado exatamente 1 vez
        verify(repository, times(1)).save(any(Estabelecimento.class));
    }

    @Test
    @DisplayName("Deve bloquear cadastro de estabelecimento com nome duplicado")
    void deveBloquearNomeDuplicado() {
        // 1. Prepara os dados
        EstabelecimentoCadastroDTO dto = new EstabelecimentoCadastroDTO("Carrefour", "Rua B");

        // Simula que o banco respondeu que JÁ EXISTE mercado com esse nome
        when(repository.existsByNomeIgnoreCase("Carrefour")).thenReturn(true);

        // 2 e 3. Executa a ação e verifica se a Exceção foi lançada
        IllegalArgumentException erro = assertThrows(IllegalArgumentException.class, () -> {
            service.cadastrar(dto);
        });

        assertEquals("Já existe um estabelecimento cadastrado com este nome.", erro.getMessage());

        // Garante que o método save() NUNCA foi chamado, protegendo o banco
        verify(repository, never()).save(any(Estabelecimento.class));
    }
}