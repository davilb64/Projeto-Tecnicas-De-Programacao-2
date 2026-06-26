package com.tp2.compras.repository;

import com.tp2.compras.model.ListaCompras;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositório de acesso a dados para {@link ListaCompras}.
 *
 * <p>Rastreamento de requisitos:
 * <ul>
 *   <li>EU008 - Eu como usuário quero poder criar, modificar e deletar listas de compras.</li>
 * </ul>
 */
@Repository
public interface ListaComprasRepository extends JpaRepository<ListaCompras, Long> {

    /**
     * Busca todas as listas de compras de um usuário.
     *
     * <p>Assertiva de entrada: usuarioId não nulo.
     * <p>Assertiva de saída: lista não nula; pode ser vazia se o usuário não tiver listas.
     *
     * <p><b>Argumentação da corretude:</b>
     * A FK usuario_id garante que todos os resultados pertencem ao usuário informado.
     * Spring Data retorna lista vazia (nunca nula) quando não há registros,
     * satisfazendo a assertiva de saída.
     *
     * @param usuarioId id do usuário dono das listas (EU008)
     * @return lista de listas de compras do usuário
     */
    List<ListaCompras> findByUsuarioId(Long usuarioId);

    /**
     * Verifica se uma lista pertence a um determinado usuário.
     * Usado para autorização antes de editar ou deletar.
     *
     * <p>Assertiva de entrada: listaId e usuarioId não nulos.
     * <p>Assertiva de saída: true se e somente se a lista existe e pertence ao usuário.
     *
     * <p><b>Argumentação da corretude:</b>
     * A query combina id (PK única) e usuario_id, portanto COUNT retorna 0 ou 1.
     * O resultado booleano é determinístico e correto para validação de propriedade.
     *
     * @param id       id da lista
     * @param usuarioId id do usuário
     * @return true se a lista pertence ao usuário (EU008)
     */
    boolean existsByIdAndUsuarioId(Long id, Long usuarioId);
}
