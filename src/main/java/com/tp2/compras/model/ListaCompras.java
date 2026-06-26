package com.tp2.compras.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade que representa uma lista de compras de um usuário.
 *
 * <p>Rastreamento de requisitos:
 * <ul>
 *   <li>EU008 - Eu como usuário quero poder criar, modificar e deletar listas de compras.</li>
 * </ul>
 */
@Entity
@Table(name = "listas_compras")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListaCompras {

    /**
     * Identificador único da lista.
     *
     * <p>Assertiva de saída: id != null após persistência.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Usuário dono da lista.
     *
     * <p>Assertiva de entrada: usuario não nulo; deve estar persistido no banco.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    /**
     * Nome da lista (ex: "Compras da semana", "Churrasco").
     *
     * <p>Assertiva de entrada: não nulo, não vazio, máximo 100 caracteres.
     */
    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nome;

    /**
     * Itens desta lista.
     *
     * <p>Assertiva de saída: lista nunca é nula; pode ser vazia.
     */
    @OneToMany(mappedBy = "lista", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ItemLista> itens = new ArrayList<>();

    /**
     * Data e hora de criação da lista.
     *
     * <p>Assertiva de saída: criadoEm != null após persistência.
     */
    @Column(name = "criado_em", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime criadoEm = LocalDateTime.now();

    /**
     * Data e hora da última modificação da lista.
     *
     * <p>Assertiva de saída: atualizadoEm != null após persistência.
     */
    @Column(name = "atualizado_em", nullable = false)
    @Builder.Default
    private LocalDateTime atualizadoEm = LocalDateTime.now();

    /**
     * Inicializa timestamps antes da primeira persistência.
     *
     * <p>Assertiva de entrada: objeto ainda não persistido.
     * <p>Assertiva de saída: criadoEm != null e atualizadoEm != null.
     *
     * <p><b>Argumentação da corretude:</b>
     * Ambas as condicionais cobrem o caso de objetos criados sem Builder.
     * criadoEm não é atualizado em operações futuras (updatable = false).
     */
    @PrePersist
    private void prePersist() {
        if (criadoEm == null) criadoEm = LocalDateTime.now();
        if (atualizadoEm == null) atualizadoEm = LocalDateTime.now();
    }

    /**
     * Atualiza atualizadoEm antes de cada UPDATE no banco.
     *
     * <p>Assertiva de entrada: objeto já persistido (id != null).
     * <p>Assertiva de saída: atualizadoEm reflete o instante da última modificação.
     *
     * <p><b>Argumentação da corretude:</b>
     * O JPA garante que @PreUpdate é chamado antes de todo UPDATE.
     * A atribuição de LocalDateTime.now() é atômica no contexto da transação,
     * portanto atualizadoEm sempre refletirá o momento da modificação.
     */
    @PreUpdate
    private void preUpdate() {
        atualizadoEm = LocalDateTime.now();
    }
}
