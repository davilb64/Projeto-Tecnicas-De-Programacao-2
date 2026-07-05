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
     * Busca todos os estabelecimentos que compõem o catálogo oficial (já aprovados).
     *
     * <p>Assertiva de saída: lista não nula contendo apenas registros com aprovado = true.
     *
     * <p><b>Argumentação da corretude:</b>
     * O Spring Data gera automaticamente a query {@code WHERE aprovado = true}.
     * O encapsulamento dessa lógica garante que a interface gráfica dos usuários finais
     * exiba estritamente os mercados validados, ocultando as sugestões pendentes.
     *
     * @return lista de estabelecimentos oficiais aprovados (EU007)
     */
    List<Estabelecimento> findByAprovadoTrue();

    /**
     * Busca os estabelecimentos pendentes de aprovação (sugeridos por usuários).
     *
     * <p>Assertiva de saída: lista não nula contendo apenas registros com aprovado = false.
     *
     * <p><b>Argumentação da corretude:</b>
     * O Spring Data gera a query {@code WHERE aprovado = false}.
     * Utilizado exclusivamente no painel de administração (EU003) para extrair o delta
     * de novas requisições sem sobrecarregar a memória com todo o catálogo de mercados.
     *
     * @return lista de estabelecimentos aguardando validação do administrador
     */
    List<Estabelecimento> findByAprovadoFalse();

    /**
     * Busca estabelecimentos cujo nome contenha o texto informado (case-insensitive),
     * ignorando se estão aprovados ou não (Busca Global).
     *
     * <p>Assertiva de entrada: nome não nulo.
     * <p>Assertiva de saída: lista não nula; pode ser vazia se nenhum resultado for encontrado.
     *
     * <p><b>Argumentação da corretude:</b>
     * Spring Data gera {@code WHERE LOWER(nome) LIKE LOWER('%nome%')}.
     * A busca parcial garante que o administrador encontre resultados digitando
     * apenas parte do nome para auditoria. O retorno é sempre uma lista, satisfazendo a assertiva de saída.
     *
     * @param nome trecho do nome a pesquisar
     * @return lista global de estabelecimentos cujo nome contém o trecho informado
     */
    List<Estabelecimento> findByNomeContainingIgnoreCase(String nome);

    /**
     * Busca apenas estabelecimentos validados (aprovados) cujo nome contenha o texto informado.
     *
     * <p>Assertiva de entrada: nome não nulo.
     * <p>Assertiva de saída: lista não nula; contendo apenas registros com aprovado = true.
     *
     * <p><b>Argumentação da corretude:</b>
     * Combina a busca parcial segura do método análogo com a filtragem de catálogo oficial.
     * Isso impede o vazamento de dados de sugestões não auditadas na barra de busca do usuário comum.
     *
     * @param nome trecho do nome a pesquisar (EU007)
     * @return lista de estabelecimentos oficiais que correspondem à busca
     */
    List<Estabelecimento> findByNomeContainingIgnoreCaseAndAprovadoTrue(String nome);

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