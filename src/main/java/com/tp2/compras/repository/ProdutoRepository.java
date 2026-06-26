package com.tp2.compras.repository;

import com.tp2.compras.model.Produto;
import com.tp2.compras.model.StatusProduto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório de acesso a dados para {@link Produto}.
 *
 * <p>Rastreamento de requisitos:
 * <ul>
 *   <li>EU004 - Eu como usuário/admin quero poder criar, editar e deletar produtos.</li>
 *   <li>EU005 - Eu como usuário/admin quero iniciar o cadastro pelo código de barras.</li>
 *   <li>EU006 - Eu como usuário/admin quero receber aviso se o produto já existe.</li>
 *   <li>EU007 - Eu como usuário quero navegar entre os produtos.</li>
 * </ul>
 */
@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    /**
     * Busca um produto pelo código de barras exato.
     *
     * <p>Assertiva de entrada: codigoBarras não nulo.
     * <p>Assertiva de saída: retorna Optional com o produto se encontrado; empty caso contrário.
     *
     * <p><b>Argumentação da corretude:</b>
     * A constraint UNIQUE em codigo_barras garante no máximo um resultado.
     * Optional nunca é nulo, satisfazendo a assertiva de saída em ambos os casos.
     *
     * @param codigoBarras código de barras a pesquisar (EU005)
     * @return Optional com o produto correspondente
     */
    Optional<Produto> findByCodigoBarras(String codigoBarras);

    /**
     * Verifica se já existe um produto com o código de barras informado.
     *
     * <p>Assertiva de entrada: codigoBarras não nulo.
     * <p>Assertiva de saída: true se e somente se existir exatamente um produto com esse código.
     *
     * <p><b>Argumentação da corretude:</b>
     * A constraint UNIQUE garante que COUNT retorna 0 ou 1.
     * O resultado booleano é determinístico e correto para detectar duplicatas (EU006).
     *
     * @param codigoBarras código de barras a verificar
     * @return true se o código já está cadastrado (EU006)
     */
    boolean existsByCodigoBarras(String codigoBarras);

    /**
     * Busca produtos pelo nome (busca parcial, case-insensitive).
     *
     * <p>Assertiva de entrada: nome não nulo.
     * <p>Assertiva de saída: lista não nula; pode ser vazia.
     *
     * @param nome trecho do nome a pesquisar (EU007)
     * @return lista de produtos cujo nome contém o trecho informado
     */
    List<Produto> findByNomeContainingIgnoreCase(String nome);

    /**
     * Busca produtos por categoria.
     *
     * <p>Assertiva de entrada: categoria não nula.
     * <p>Assertiva de saída: lista não nula; pode ser vazia.
     *
     * @param categoria categoria a filtrar (EU007)
     * @return lista de produtos da categoria informada
     */
    List<Produto> findByCategoriaIgnoreCase(String categoria);

    /**
     * Busca produtos por status de aprovação.
     *
     * <p>Assertiva de entrada: status não nulo.
     * <p>Assertiva de saída: lista não nula; pode ser vazia.
     *
     * @param status status a filtrar (EU004)
     * @return lista de produtos com o status informado
     */
    List<Produto> findByStatus(StatusProduto status);
}
