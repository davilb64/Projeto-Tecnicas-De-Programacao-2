package com.tp2.compras.repository;

import com.tp2.compras.model.ItemLista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositório de acesso a dados para {@link ItemLista}.
 *
 * <p>Rastreamento de requisitos:
 * <ul>
 *   <li>EU008 - Eu como usuário quero poder criar, modificar e deletar listas de compras.</li>
 * </ul>
 */
@Repository
public interface ItemListaRepository extends JpaRepository<ItemLista, Long> {

    /**
     * Busca todos os itens de uma lista de compras.
     *
     * <p>Assertiva de entrada: listaId não nulo.
     * <p>Assertiva de saída: lista não nula; pode ser vazia.
     *
     * <p><b>Argumentação da corretude:</b>
     * A FK lista_id garante que todos os resultados pertencem à lista informada.
     * O CASCADE DELETE na tabela garante que itens órfãos não existem no banco.
     *
     * @param listaId id da lista (EU008)
     * @return lista de itens da lista de compras
     */
    List<ItemLista> findByListaId(Long listaId);

    /**
     * Verifica se uma variação já está na lista (evita duplicatas).
     *
     * <p>Assertiva de entrada: listaId e variacaoId não nulos.
     * <p>Assertiva de saída: true se e somente se o item já existe na lista.
     *
     * <p><b>Argumentação da corretude:</b>
     * A constraint UNIQUE(lista_id, variacao_id) na tabela garante COUNT 0 ou 1.
     * O resultado é determinístico e correto para validação antes de inserir novo item.
     *
     * @param listaId   id da lista
     * @param variacaoId id da variação do produto
     * @return true se a variação já está na lista (EU008)
     */
    boolean existsByListaIdAndVariacaoId(Long listaId, Long variacaoId);
}
