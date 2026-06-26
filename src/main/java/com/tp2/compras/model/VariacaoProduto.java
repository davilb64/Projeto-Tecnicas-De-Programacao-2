package com.tp2.compras.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade que representa uma variação específica de um produto (ex: "1kg", "500ml").
 * Cada variação possui seu próprio código de barras.
 *
 * <p>Rastreamento de requisitos:
 * <ul>
 *   <li>EU006 - Eu como admin quero agrupar variações de um mesmo produto (Peso/Tamanho).</li>
 *   <li>EU005 - Cada variação pode ser identificada pelo seu código de barras.</li>
 * </ul>
 */
@Entity
@Table(name = "variacoes_produto")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VariacaoProduto {

    /**
     * Identificador único da variação.
     *
     * <p>Assertiva de saída: id != null após persistência.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Produto pai ao qual esta variação pertence.
     *
     * <p>Assertiva de entrada: produto não nulo; deve estar persistido no banco.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    /**
     * Descrição da variação (ex: "1kg", "500ml", "Grande").
     *
     * <p>Assertiva de entrada: não nulo, não vazio, máximo 100 caracteres.
     */
    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String descricao;

    /**
     * Peso numérico da variação.
     *
     * <p>Assertiva de entrada: pode ser nulo; se informado, deve ser positivo.
     */
    @Column(precision = 10, scale = 3)
    private BigDecimal peso;

    /**
     * Unidade de medida (ex: "g", "kg", "ml", "L").
     *
     * <p>Assertiva de entrada: pode ser nulo; se informado, máximo 20 caracteres.
     */
    @Size(max = 20)
    @Column(length = 20)
    private String unidade;

    /**
     * Código de barras único desta variação (EU005).
     *
     * <p>Assertiva de entrada: se informado, deve ser único globalmente e ter no máximo 50 caracteres.
     * <p>Assertiva de saída: dois registros com o mesmo codigo_barras não podem coexistir.
     */
    @Size(max = 50)
    @Column(name = "codigo_barras", unique = true, length = 50)
    private String codigoBarras;

    /**
     * Data e hora de criação do registro.
     *
     * <p>Assertiva de saída: criadoEm != null após persistência.
     */
    @Column(name = "criado_em", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime criadoEm = LocalDateTime.now();

    /**
     * Define criadoEm antes da primeira persistência.
     *
     * <p>Assertiva de entrada: objeto ainda não persistido.
     * <p>Assertiva de saída: criadoEm != null.
     *
     * <p><b>Argumentação da corretude:</b>
     * A condicional garante que criadoEm nunca seja nulo na persistência,
     * independente do caminho de construção do objeto.
     */
    @PrePersist
    private void prePersist() {
        if (criadoEm == null) {
            criadoEm = LocalDateTime.now();
        }
    }
}
