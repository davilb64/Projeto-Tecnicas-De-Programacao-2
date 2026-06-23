package com.tp2.compras.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidade que representa um usuário do sistema de compras.
 *
 * <p>Rastreamento de requisitos:
 * <ul>
 *   <li>EU001 - Eu como usuário quero poder criar uma conta no site para poder interagir.</li>
 *   <li>EU002 - Eu como usuário quero poder fazer login no site se já possuo uma conta e cair na tela principal.</li>
 * </ul>
 */
@Entity
@Table(name = "usuarios")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    /**
     * Identificador único do usuário, gerado automaticamente pelo banco.
     *
     * <p>Assertiva de saída: id != null após persistência.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nome completo do usuário.
     *
     * <p>Assertiva de entrada: nome não nulo e não vazio, máximo 100 caracteres.
     */
    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nome;

    /**
     * Endereço de e-mail do usuário. Utilizado como identificador no login (EU002).
     *
     * <p>Assertiva de entrada: email não nulo, formato válido, único no sistema, máximo 150 caracteres.
     */
    @NotBlank
    @Email
    @Size(max = 150)
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    /**
     * Hash BCrypt da senha do usuário. Nunca armazena a senha em texto puro.
     *
     * <p>Assertiva de entrada: senhaHash não nulo, gerado via BCrypt.
     * <p>Assertiva de saída: valor armazenado é irreversível (hash unidirecional).
     */
    @NotBlank
    @Column(name = "senha_hash", nullable = false)
    private String senhaHash;

    /**
     * Papel do usuário no sistema: USUARIO ou ADMINISTRADOR.
     *
     * <p>Assertiva de saída: papel nunca é nulo; padrão é USUARIO ao criar conta (EU001).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Papel papel = Papel.USUARIO;

    /**
     * Data e hora de criação do cadastro.
     *
     * <p>Assertiva de saída: criadoEm nunca é nulo após persistência; não é alterado após criação.
     */
    @Column(name = "criado_em", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime criadoEm = LocalDateTime.now();

    /**
     * Garante que criadoEm seja preenchido antes da primeira persistência.
     *
     * <p><b>Assertiva de entrada:</b> objeto ainda não persistido (id == null);
     * criadoEm pode ser nulo se o objeto foi construído sem o Builder.
     *
     * <p><b>Assertiva de saída:</b> criadoEm != null.
     *
     * <p><b>Argumentação da corretude:</b>
     * <ol>
     *   <li>A anotação {@code @PrePersist} garante que este método é chamado pelo JPA
     *       imediatamente antes de qualquer operação INSERT no banco — nunca em UPDATEs,
     *       pois a coluna possui {@code updatable = false}.</li>
     *   <li>O único estado possível de criadoEm ao entrar na função é nulo (quando o objeto
     *       foi criado via construtor padrão sem Builder) ou não-nulo (quando criado via Builder,
     *       que já inicializa com {@code LocalDateTime.now()}).</li>
     *   <li>A condicional {@code if (criadoEm == null)} cobre o único caso em que o campo
     *       poderia ser persistido nulo, atribuindo o instante atual.</li>
     *   <li>Após a execução, criadoEm é necessariamente não-nulo em ambos os ramos
     *       (nulo → atribuído; não-nulo → mantido), satisfazendo a assertiva de saída.</li>
     * </ol>
     */
    @PrePersist
    private void prePersist() {
        if (criadoEm == null) {
            criadoEm = LocalDateTime.now();
        }
    }
}
