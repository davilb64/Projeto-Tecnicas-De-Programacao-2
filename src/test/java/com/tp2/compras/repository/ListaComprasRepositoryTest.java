package com.tp2.compras.repository;

import com.tp2.compras.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de repositório para {@link ListaComprasRepository} e {@link ItemListaRepository}.
 *
 * <p>Rastreamento de requisitos:
 * <ul>
 *   <li>EU008 - Criar, modificar e deletar listas de compras.</li>
 * </ul>
 */
@DataJpaTest
@ActiveProfiles("test")
class ListaComprasRepositoryTest {

    @Autowired
    private ListaComprasRepository listaRepo;

    @Autowired
    private ItemListaRepository itemRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private ProdutoRepository produtoRepo;

    @Autowired
    private VariacaoProdutoRepository variacaoRepo;

    private Usuario usuario;
    private VariacaoProduto variacao;

    @BeforeEach
    void setUp() {
        usuario = usuarioRepo.save(Usuario.builder()
                .nome("Maria")
                .email("maria@email.com")
                .senhaHash("hash")
                .build());

        Produto produto = produtoRepo.save(Produto.builder()
                .nome("Arroz")
                .build());

        variacao = variacaoRepo.save(VariacaoProduto.builder()
                .produto(produto)
                .descricao("1kg")
                .build());
    }

    /** EU008 - Deve criar lista de compras para o usuário. */
    @Test
    @DisplayName("EU008 - Deve criar lista de compras")
    void deveCriarLista() {
        ListaCompras lista = listaRepo.save(ListaCompras.builder()
                .usuario(usuario)
                .nome("Compras da semana")
                .build());

        assertThat(lista.getId()).isNotNull();
        assertThat(lista.getCriadoEm()).isNotNull();
        assertThat(lista.getAtualizadoEm()).isNotNull();
    }

    /** EU008 - Deve buscar todas as listas de um usuário. */
    @Test
    @DisplayName("EU008 - Deve buscar listas do usuário")
    void deveBuscarListasDoUsuario() {
        listaRepo.save(ListaCompras.builder().usuario(usuario).nome("Lista 1").build());
        listaRepo.save(ListaCompras.builder().usuario(usuario).nome("Lista 2").build());

        List<ListaCompras> listas = listaRepo.findByUsuarioId(usuario.getId());

        assertThat(listas).hasSize(2);
    }

    /** EU008 - Deve verificar propriedade da lista antes de editar. */
    @Test
    @DisplayName("EU008 - Deve verificar se lista pertence ao usuário")
    void deveVerificarPropriedadeDaLista() {
        ListaCompras lista = listaRepo.save(ListaCompras.builder()
                .usuario(usuario)
                .nome("Minha lista")
                .build());

        assertTrue(listaRepo.existsByIdAndUsuarioId(lista.getId(), usuario.getId()));
        assertFalse(listaRepo.existsByIdAndUsuarioId(lista.getId(), 999L));
    }

    /** EU008 - Deve adicionar item à lista e detectar duplicata. */
    @Test
    @DisplayName("EU008 - Deve adicionar item e detectar duplicata")
    void deveAdicionarItemEDetectarDuplicata() {
        ListaCompras lista = listaRepo.save(ListaCompras.builder()
                .usuario(usuario)
                .nome("Lista teste")
                .build());

        itemRepo.save(ItemLista.builder()
                .lista(lista)
                .variacao(variacao)
                .quantidade(2)
                .build());

        assertTrue(itemRepo.existsByListaIdAndVariacaoId(lista.getId(), variacao.getId()));
    }

    /** EU008 - Deve deletar lista e seus itens em cascata. */
    @Test
    @DisplayName("EU008 - Deve deletar lista e itens em cascata")
    void deveDeletarListaEmCascata() {
        ListaCompras lista = listaRepo.save(ListaCompras.builder()
                .usuario(usuario)
                .nome("Lista a deletar")
                .build());

        itemRepo.save(ItemLista.builder()
                .lista(lista)
                .variacao(variacao)
                .build());

        listaRepo.deleteById(lista.getId());

        assertFalse(listaRepo.existsById(lista.getId()));
        assertThat(itemRepo.findByListaId(lista.getId())).isEmpty();
    }
}
