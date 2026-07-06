package com.tp2.compras.repository;

import com.tp2.compras.model.VariacaoProduto;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório de acesso a dados para {@link VariacaoProduto}.
 *
 * <p>Rastreamento de requisitos:
 * <ul>
 *   <li>EU005 - Cadastro de variação pelo código de barras.</li>
 *   <li>EU006 - Agrupamento e detecção de duplicatas entre variações.</li>
 * </ul>
 */
@Repository
public interface VariacaoProdutoRepository extends JpaRepository<VariacaoProduto, Long> {

  /**
   * Busca todas as variações de um produto pelo id do produto pai.
   *
   * <p>Assertiva de entrada: produtoId não nulo.
   *
   * <p>Assertiva de saída: lista não nula; pode ser vazia.
   *
   * <p><b>Argumentação da corretude:</b>
   * A FK produto_id garante que todos os resultados pertencem ao produto informado.
   * Spring Data retorna lista vazia (nunca nula) quando não há registros.
   *
   * @param produtoId id do produto pai (EU006)
   * @return lista de variações do produto
   */
  List<VariacaoProduto> findByProdutoId(Long produtoId);

    /**
   * Verifica se já existe uma variação com o código de barras informado.
   *
   * <p>Assertiva de entrada: codigoBarras não nulo.
   *
   * <p>Assertiva de saída: true se e somente se existir exatamente uma variação com esse código.
   *
   * @param codigoBarras código de barras a verificar (EU006)
   * @return true se o código já está cadastrado
   */
  boolean existsByCodigoBarras(String codigoBarras);
}
