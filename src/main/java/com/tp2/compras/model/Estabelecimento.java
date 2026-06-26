package com.tp2.compras.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidade que representa um estabelecimento comercial no sistema.
 *
 * <p>Rastreamento de requisitos:
 * <ul>
 *   <li>EU003 - Eu como usuário/admin quero poder criar, editar e deletar estabelecimentos.</li>
 * </ul>
 */
@Entity
@Table(name = "estabelecimentos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Estabelecimento {

    /**
     * Identificador único do estabelecimento.
     *
     * <p>Assertiva de saída: id != null após persistência.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nome do estabelecimento.
     *
     * <p>Assertiva de entrada: nome não nulo, não vazio, máximo 150 caracteres.
     */
    @NotBlank
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String nome;

    /**
     * Endereço do estabelecimento.
     *
     * <p>Assertiva de entrada: endereço pode ser nulo; se informado, máximo 255 caracteres.
     */
    @Size(max = 255)
    @Column(length = 255)
    private String endereco;

    /**
     * Data e hora de criação do registro.
     *
     * <p>Assertiva de saída: criadoEm != null após persistência.
     */
    @Column(name = "criado_em", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime criadoEm = LocalDateTime.now();

    /**
     * Define criadoEm antes da primeira persistência caso não tenha sido inicializado.
     *
     * <p>Assertiva de entrada: objeto ainda não persistido.
     * <p>Assertiva de saída: criadoEm != null.
     *
     * <p><b>Argumentação da corretude:</b>
     * A condicional garante que criadoEm nunca seja persistido como nulo,
     * cobrindo o caso de objetos criados sem o Builder.
     */
    @PrePersist
    private void prePersist() {
        if (criadoEm == null) {
            criadoEm = LocalDateTime.now();
        }
    }
}
