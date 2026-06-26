package com.tp2.compras.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade que representa um produto genérico no sistema.
 * Variações específicas (peso/tamanho) são armazenadas em {@link VariacaoProduto}.
 *
 * <p>Rastreamento de requisitos:
 * <ul>
 *   <li>EU004 - Eu como usuário/admin quero poder criar, editar e deletar produtos.</li>
 *   <li>EU005 - Eu como usuário/admin quero iniciar o cadastro pelo código de barras.</li>
 *   <li>EU006 - Eu como usuário/admin quero receber aviso se o produto já existe.</li>
 * </ul>
 */
@Entity
@Table(name = "produtos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Produto {

    /**
     * Identificador único do produto.
     *
     * <p>Assertiva de saída: id != null após persistência.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nome do produto.
     *
     * <p>Assertiva de entrada: nome não nulo, não vazio, máximo 150 caracteres.
     */
    @NotBlank
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String nome;

    /**
     * Código de barras do produto. Usado para identificar duplicatas (EU005, EU006).
     *
     * <p>Assertiva de entrada: se informado, deve ser único no sistema e ter no máximo 50 caracteres.
     * <p>Assertiva de saída: dois produtos com o mesmo codigo_barras não podem coexistir no banco.
     */
    @Size(max = 50)
    @Column(name = "codigo_barras", unique = true, length = 50)
    private String codigoBarras;

    /**
     * Descrição detalhada do produto.
     *
     * <p>Assertiva de entrada: pode ser nula.
     */
    @Column(columnDefinition = "TEXT")
    private String descricao;

    /**
     * Categoria do produto (ex: "Laticínios", "Bebidas").
     *
     * <p>Assertiva de entrada: pode ser nula; se informada, máximo 100 caracteres.
     */
    @Size(max = 100)
    @Column(length = 100)
    private String categoria;

    /**
     * Status de aprovação do produto pelo administrador (EU004).
     *
     * <p>Assertiva de saída: status nunca é nulo; padrão PENDENTE ao ser criado por usuário.
     */
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private StatusProduto status = StatusProduto.PENDENTE;

    /**
     * Usuário que cadastrou o produto.
     *
     * <p>Assertiva de entrada: pode ser nulo (cadastro pelo admin direto).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criado_por")
    private Usuario criadoPor;

    /**
     * Variações deste produto (peso, tamanho, etc.).
     *
     * <p>Assertiva de saída: lista nunca é nula; pode ser vazia.
     */
    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<VariacaoProduto> variacoes = new ArrayList<>();

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
     * Mesma lógica de {@code Usuario.prePersist()}: a condicional cobre o único
     * caso em que criadoEm poderia ser nulo (construtor padrão sem Builder).
     */
    @PrePersist
    private void prePersist() {
        if (criadoEm == null) {
            criadoEm = LocalDateTime.now();
        }
    }
}
