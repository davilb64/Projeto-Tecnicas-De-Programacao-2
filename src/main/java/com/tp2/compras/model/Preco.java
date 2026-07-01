package com.tp2.compras.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade que representa o registro de um preço histórico de um produto.
 * É a base do sistema colaborativo, conectando o que foi comprado, onde, por quem e por quanto.
 *
 * <p>Rastreamento de requisitos:
 * <ul>
 *   <li>EU009 - Eu como usuário quero poder informar o preço de um produto em um mercado.</li>
 *   <li>EU010 - Eu como sistema quero cruzar os preços informados para comparar o valor das listas.</li>
 * </ul>
 */
@Entity
@Table(name = "precos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Preco {

    /**
     * Identificador único do registro de preço.
     *
     * <p>Assertiva de saída: id != null após persistência.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * A variação específica do produto (ex: Coca-Cola 2L) que foi comprada.
     *
     * <p>Assertiva de entrada: não nulo, deve referenciar um ID válido na tabela variacoes_produto.
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variacao_id", nullable = false)
    private VariacaoProduto variacao;

    /**
     * O mercado/estabelecimento onde o preço foi encontrado.
     *
     * <p>Assertiva de entrada: não nulo, deve referenciar um ID válido na tabela estabelecimentos.
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estabelecimento_id", nullable = false)
    private Estabelecimento estabelecimento;

    /**
     * O usuário da comunidade que informou este preço.
     *
     * <p>Assertiva de entrada: não nulo, deve referenciar um ID válido na tabela usuarios.
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario registradoPor;

    /**
     * O valor monetário pago ou visto na prateleira.
     *
     * <p>Assertiva de entrada: valor não nulo e estritamente maior que zero (>= 0.01).
     */
    @NotNull
    @DecimalMin(value = "0.01", message = "O valor do produto deve ser maior que zero")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    /**
     * Data e hora exata em que o preço foi informado.
     * Usado para garantir que a comparação (EU010) pegue os preços mais recentes.
     *
     * <p>Assertiva de saída: dataRegistro != null após persistência.
     */
    @Column(name = "data_registro", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime dataRegistro = LocalDateTime.now();

    /**
     * Define a data de registro antes da primeira persistência, caso não tenha sido injetada.
     *
     * <p>Assertiva de entrada: objeto ainda não persistido no banco.
     * <p>Assertiva de saída: dataRegistro != null com a data/hora atual.
     *
     * <p><b>Argumentação da corretude:</b>
     * A condicional evita a sobrescrita de datas em casos de migração manual,
     * mas assegura que nenhum registro colaborativo fique sem timestamp,
     * o que quebraria a lógica temporal de cruzamento de preços.
     */
    @PrePersist
    private void prePersist() {
        if (dataRegistro == null) {
            dataRegistro = LocalDateTime.now();
        }
    }
}