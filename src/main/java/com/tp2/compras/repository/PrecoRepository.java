package com.tp2.compras.repository;

import com.tp2.compras.model.Preco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório de acesso a dados para a entidade {@link Preco}.
 * Responsável por persistir novos registros e executar as queries complexas
 * de comparação de valores para o motor de busca do aplicativo.
 *
 * <p>Rastreamento de requisitos:
 * <ul>
 *   <li>EU009 - Eu como usuário quero poder informar o preço de um produto em um mercado.</li>
 *   <li>EU010 - Eu como sistema quero cruzar os preços informados para comparar o valor das listas.</li>
 * </ul>
 */
@Repository
public interface PrecoRepository extends JpaRepository<Preco, Long> {

    /**
     * Busca o registro de preço MAIS RECENTE de uma variação específica dentro de um mercado específico.
     * Utilizado para calcular quanto a lista do usuário custaria hoje naquele mercado (EU010).
     *
     * <p><b>Assertiva de entrada:</b> variacaoId e estabelecimentoId não nulos.
     * <p><b>Assertiva de saída:</b> Optional contendo o Preco mais recente se existir; Optional empty caso contrário.
     *
     * <p><b>Argumentação da corretude:</b>
     * A palavra-chave "First" combinada com "OrderByDataRegistroDesc" instrui o Hibernate a gerar
     * um SQL com "ORDER BY data_registro DESC LIMIT 1". A performance dessa query é garantida
     * pelos índices {@code idx_precos_variacao}, {@code idx_precos_estabelecimento} e
     * {@code idx_precos_data_registro} criados na Migration V6.
     *
     * @param variacaoId ID da variação do produto
     * @param estabelecimentoId ID do supermercado
     * @return O preço mais recente informado para este item neste mercado
     */
    Optional<Preco> findFirstByVariacaoIdAndEstabelecimentoIdOrderByDataRegistroDesc(Long variacaoId, Long estabelecimentoId);

    /**
     * Busca o MENOR PREÇO MAIS RECENTE de uma variação entre TODOS os mercados.
     * Utilizado para mostrar ao usuário onde o item unitário está mais barato no momento.
     *
     * <p><b>Assertiva de entrada:</b> variacaoId não nulo.
     * <p><b>Assertiva de saída:</b> Optional contendo o Preco mais baixo se existir; Optional empty caso contrário.
     *
     * <p><b>Argumentação da corretude:</b>
     * O Spring Data constrói a query ordenando primeiro pelo valor (do menor para o maior) e,
     * em caso de empate, pega o registro mais recente. "LIMIT 1" garante que retorne apenas o melhor.
     *
     * @param variacaoId ID da variação do produto
     * @return O menor preço registrado para esta variação
     */
    Optional<Preco> findFirstByVariacaoIdOrderByValorAscDataRegistroDesc(Long variacaoId);

    /**
     * Busca o histórico completo de preços de uma variação em um mercado específico, do mais novo pro mais antigo.
     * Útil caso você queira implementar um gráfico de variação de preços no futuro (inflação do produto).
     *
     * <p><b>Assertiva de entrada:</b> variacaoId e estabelecimentoId não nulos.
     * <p><b>Assertiva de saída:</b> Lista de Precos ordenada por data; lista vazia se nunca foi registrado.
     *
     * @param variacaoId ID da variação do produto
     * @param estabelecimentoId ID do supermercado
     * @return Lista do histórico de preços
     */
    List<Preco> findByVariacaoIdAndEstabelecimentoIdOrderByDataRegistroDesc(Long variacaoId, Long estabelecimentoId);

    /**
     * Query customizada (JPQL) para buscar os preços mais recentes de uma LISTA inteira em um mercado.
     * Otimização extrema para a EU010: em vez de fazer um SELECT (findFirst) para cada item da lista,
     * fazemos um único SELECT trazendo os preços mais atuais de todos os itens de uma vez.
     *
     * <p><b>Assertiva de entrada:</b> lista de variacaoIds não nula e estabelecimentoId não nulo.
     * <p><b>Assertiva de saída:</b> Lista contendo apenas o Preco mais recente de cada variação solicitada.
     *
     * <p><b>Argumentação da corretude:</b>
     * A subquery correlacionada garante que, para cada variação encontrada naquele estabelecimento,
     * apenas a linha com a data_registro máxima (MAX) seja retornada.
     *
     * @param variacaoIds Lista com os IDs das variações que estão no carrinho do usuário
     * @param estabelecimentoId ID do supermercado que está sendo comparado
     * @return Lista com os preços mais recentes de cada item solicitado
     */
    @Query("""
        SELECT p FROM Preco p 
        WHERE p.estabelecimento.id = :estabelecimentoId 
          AND p.variacao.id IN :variacaoIds 
          AND p.dataRegistro = (
              SELECT MAX(p2.dataRegistro) 
              FROM Preco p2 
              WHERE p2.variacao.id = p.variacao.id 
                AND p2.estabelecimento.id = :estabelecimentoId
          )
    """)
    List<Preco> buscarPrecosAtuaisDaListaNoMercado(
            @Param("variacaoIds") List<Long> variacaoIds,
            @Param("estabelecimentoId") Long estabelecimentoId
    );

    /**
     * Calcula a média de preço de uma variação em todos os mercados.
     */
    @Query("SELECT AVG(p.valor) FROM Preco p WHERE p.variacao.id = :variacaoId")
    Double calcularMediaPorVariacao(@Param("variacaoId") Long variacaoId);
}