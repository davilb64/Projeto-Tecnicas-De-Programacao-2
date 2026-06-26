package com.tp2.compras.repository;

import com.tp2.compras.model.Estabelecimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositório de acesso a dados para {@link Estabelecimento}.
 *
 * <p>Rastreamento de requisitos:
 * <ul>
 *   <li>EU003 - Eu como usuário/admin quero poder criar, editar e deletar estabelecimentos.</li>
 *   <li>EU007 - Eu como usuário quero navegar entre os estabelecimentos.</li>
 * </ul>
 */
@Repository
public interface EstabelecimentoRepository extends JpaRepository<Estabelecimento, Long> {

    /**
     * Busca estabelecimentos cujo nome contenha o texto informado (case-insensitive).
     *
     * <p>Assertiva de entrada: nome não nulo.
     * <p>Assertiva de saída: lista não nula; pode ser vazia se nenhum resultado for encontrado.
     *
     * <p><b>Argumentação da corretude:</b>
     * Spring Data gera {@code WHERE LOWER(nome) LIKE LOWER('%nome%')}.
     * A busca parcial garante que o usuário encontre resultados digitando apenas parte do nome.
     * O retorno é sempre uma lista (nunca nulo), satisfazendo a assertiva de saída.
     *
     * @param nome trecho do nome a pesquisar (EU007)
     * @return lista de estabelecimentos cujo nome contém o trecho informado
     */
    List<Estabelecimento> findByNomeContainingIgnoreCase(String nome);

    /**
     * Verifica se já existe um estabelecimento com o nome exato informado.
     *
     * <p>Assertiva de entrada: nome não nulo.
     * <p>Assertiva de saída: retorna true se existir ao menos um registro com o nome exato.
     *
     * @param nome nome a verificar
     * @return true se o nome já está cadastrado (EU003)
     */
    boolean existsByNomeIgnoreCase(String nome);
}
