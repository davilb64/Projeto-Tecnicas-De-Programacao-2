package com.tp2.compras.repository;

import com.tp2.compras.model.Estabelecimento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de repositório para {@link EstabelecimentoRepository}.
 *
 * <p>Rastreamento de requisitos:
 * <ul>
 *   <li>EU003 - Criar, editar e deletar estabelecimentos.</li>
 *   <li>EU007 - Navegação entre estabelecimentos.</li>
 * </ul>
 */
@DataJpaTest
@ActiveProfiles("test")
class EstabelecimentoRepositoryTest {

    @Autowired
    private EstabelecimentoRepository repository;

    /** EU003 - Deve salvar e recuperar um estabelecimento. */
    @Test
    @DisplayName("EU003 - Deve salvar novo estabelecimento")
    void deveSalvarEstabelecimento() {
        Estabelecimento e = Estabelecimento.builder()
                .nome("Supermercado Central")
                .endereco("Rua A, 100")
                .build();

        Estabelecimento salvo = repository.save(e);

        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getNome()).isEqualTo("Supermercado Central");
        assertThat(salvo.getCriadoEm()).isNotNull();
    }

    /** EU007 - Deve buscar estabelecimentos por trecho do nome. */
    @Test
    @DisplayName("EU007 - Deve buscar por nome parcial")
    void deveBuscarPorNomeParcial() {
        repository.save(Estabelecimento.builder().nome("Supermercado Central").build());
        repository.save(Estabelecimento.builder().nome("Mercado Bom Preço").build());

        List<Estabelecimento> resultado = repository.findByNomeContainingIgnoreCase("mercado");

        assertThat(resultado).hasSize(2);
    }

    /** EU003 - Deve detectar nome duplicado. */
    @Test
    @DisplayName("EU003 - Deve detectar nome já existente")
    void deveDetectarNomeDuplicado() {
        repository.save(Estabelecimento.builder().nome("Carrefour").build());

        assertTrue(repository.existsByNomeIgnoreCase("carrefour"));
        assertFalse(repository.existsByNomeIgnoreCase("Extra"));
    }

    /** EU003 - Deve deletar estabelecimento. */
    @Test
    @DisplayName("EU003 - Deve deletar estabelecimento")
    void deveDeletarEstabelecimento() {
        Estabelecimento salvo = repository.save(
                Estabelecimento.builder().nome("Loja X").build());

        repository.deleteById(salvo.getId());

        assertFalse(repository.existsById(salvo.getId()));
    }
}
