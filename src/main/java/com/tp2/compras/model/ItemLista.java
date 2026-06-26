package com.tp2.compras.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade que representa um item dentro de uma lista de compras.
 * Liga uma {@link ListaCompras} a uma {@link VariacaoProduto}.
 *
 * <p>Rastreamento de requisitos:
 * <ul>
 *   <li>EU008 - Eu como usuário quero poder criar, modificar e deletar listas de compras.</li>
 * </ul>
 */
@Entity
@Table(
    name = "itens_lista",
    uniqueConstraints = @UniqueConstraint(columnNames = {"lista_id", "variacao_id"})
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemLista {

    /**
     * Identificador único do item.
     *
     * <p>Assertiva de saída: id != null após persistência.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Lista à qual este item pertence.
     *
     * <p>Assertiva de entrada: lista não nula; deve estar persistida no banco.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lista_id", nullable = false)
    private ListaCompras lista;

    /**
     * Variação do produto selecionada para este item.
     *
     * <p>Assertiva de entrada: variacao não nula; deve estar persistida no banco.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "variacao_id", nullable = false)
    private VariacaoProduto variacao;

    /**
     * Quantidade desejada do item.
     *
     * <p>Assertiva de entrada: quantidade >= 1.
     * <p>Assertiva de saída: quantidade persistida é sempre positiva.
     */
    @Min(1)
    @Column(nullable = false)
    @Builder.Default
    private Integer quantidade = 1;

    /**
     * Indica se o item já foi colocado no carrinho durante a compra.
     *
     * <p>Assertiva de saída: comprado nunca é nulo; padrão false ao criar o item.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean comprado = false;
}
