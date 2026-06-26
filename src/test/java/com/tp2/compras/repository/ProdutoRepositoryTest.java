package com.tp2.compras.repository;

import com.tp2.compras.model.Produto;
import com.tp2.compras.model.StatusProduto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de repositório para {@link ProdutoRepository}.
 *
 * <p>Rastreamento de requisitos:
 * <ul>
 *   <li>EU004 - Criar, editar e deletar produtos.</li>
 *   <li>EU005 - Cadastro pelo código de barras.</li>
 *   <li>EU006 - Detecção de produto duplicado.</li>
 *   <li>EU007 - Navegação/filtro entre produtos.</li>
 * </ul>
 */
@DataJpaTest
@ActiveProfiles("test")
class ProdutoRepositoryTest {

    @Autowired
    private ProdutoRepository repository;

    /** EU004 - Deve salvar produto com status PENDENTE por padrão. */
    @Test
    @DisplayName("EU004 - Deve salvar produto com status PENDENTE")
    void deveSalvarProduto() {
        Produto p = Produto.builder()
                .nome("Arroz Tio João")
                .categoria("Grãos")
                .build();

        Produto salvo = repository.save(p);

        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getStatus()).isEqualTo(StatusProduto.PENDENTE);
        assertThat(salvo.getCriadoEm()).isNotNull();
    }

    /** EU005 - Deve encontrar produto pelo código de barras. */
    @Test
    @DisplayName("EU005 - Deve encontrar produto pelo código de barras")
    void deveEncontrarPorCodigoBarras() {
        repository.save(Produto.builder()
                .nome("Feijão Carioca")
                .codigoBarras("7891234560001")
                .build());

        Optional<Produto> resultado = repository.findByCodigoBarras("7891234560001");

        assertTrue(resultado.isPresent());
        assertThat(resultado.get().getNome()).isEqualTo("Feijão Carioca");
    }

    /** EU006 - Deve detectar código de barras duplicado. */
    @Test
    @DisplayName("EU006 - Deve detectar código de barras já existente")
    void deveDetectarCodigoBarrasDuplicado() {
        repository.save(Produto.builder()
                .nome("Leite Integral")
                .codigoBarras("7891234560002")
                .build());

        assertTrue(repository.existsByCodigoBarras("7891234560002"));
        assertFalse(repository.existsByCodigoBarras("0000000000000"));
    }

    /** EU007 - Deve filtrar produtos por nome parcial. */
    @Test
    @DisplayName("EU007 - Deve buscar produtos por nome parcial")
    void deveBuscarPorNome() {
        repository.save(Produto.builder().nome("Arroz Branco").build());
        repository.save(Produto.builder().nome("Arroz Integral").build());
        repository.save(Produto.builder().nome("Feijão Preto").build());

        List<Produto> resultado = repository.findByNomeContainingIgnoreCase("arroz");

        assertThat(resultado).hasSize(2);
    }

    /** EU007 - Deve filtrar produtos por categoria. */
    @Test
    @DisplayName("EU007 - Deve filtrar produtos por categoria")
    void deveFiltrarPorCategoria() {
        repository.save(Produto.builder().nome("Coca-Cola").categoria("Bebidas").build());
        repository.save(Produto.builder().nome("Suco de Laranja").categoria("Bebidas").build());
        repository.save(Produto.builder().nome("Arroz").categoria("Grãos").build());

        List<Produto> resultado = repository.findByCategoriaIgnoreCase("bebidas");

        assertThat(resultado).hasSize(2);
    }

    /** EU004 - Deve filtrar produtos por status. */
    @Test
    @DisplayName("EU004 - Deve filtrar produtos por status")
    void deveFiltrarPorStatus() {
        repository.save(Produto.builder().nome("Produto A").status(StatusProduto.APROVADO).build());
        repository.save(Produto.builder().nome("Produto B").status(StatusProduto.PENDENTE).build());

        List<Produto> aprovados = repository.findByStatus(StatusProduto.APROVADO);

        assertThat(aprovados).hasSize(1);
        assertThat(aprovados.get(0).getNome()).isEqualTo("Produto A");
    }
}
