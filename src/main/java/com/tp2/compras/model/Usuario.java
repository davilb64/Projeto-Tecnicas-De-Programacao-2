package com.tp2.compras.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

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
public class Usuario implements UserDetails {

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
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
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

    // =========================================================
    // MÉTODOS OBRIGATÓRIOS DA INTERFACE UserDetails
    // =========================================================

    /**
     * Retorna os privilégios (roles) concedidos ao usuário. Essencial para o controle
     * de acesso baseado em rotas do Spring Security.
     *
     * <p><b>Assertiva de saída:</b> retorna uma coleção não nula contendo exatamente um
     * objeto do tipo SimpleGrantedAuthority correspondente ao papel do usuário.
     *
     * <p><b>Argumentação da corretude:</b>
     * O Spring Security utiliza o prefixo "ROLE_" por padrão para identificar regras de autorização
     * (ex: hasRole('USUARIO')). Ao concatenar "ROLE_" com o nome da constante do enum Papel
     * (que é garantido de não ser nulo), garantimos compatibilidade nativa com os filtros de segurança.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.papel.name()));
    }

    /**
     * Informa ao framework qual atributo interno da entidade armazena a credencial (senha).
     *
     * <p><b>Assertiva de saída:</b> retorna o atributo {@code senhaHash}.
     */
    @Override
    public String getPassword() {
        return this.senhaHash;
    }

    /**
     * Informa ao framework qual atributo da entidade é utilizado como nome de usuário
     * no processo de autenticação.
     *
     * <p><b>Assertiva de saída:</b> retorna o valor de {@code email} (satisfazendo o requisito EU002).
     */
    @Override
    public String getUsername() {
        return this.email;
    }

    /**
     * Indica se a conta do usuário está expirada.
     *
     * <p><b>Assertiva de saída:</b> retorna {@code true}. Como o sistema atual não possui política
     * de expiração de contas, todas as contas são consideradas não expiradas por padrão.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indica se a conta do usuário está bloqueada.
     *
     * <p><b>Assertiva de saída:</b> retorna {@code true}. Como o sistema atual não implementa
     * bloqueio temporário (ex: erro excessivo de tentativas de login), nenhuma conta é bloqueada.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indica se a credencial (senha) do usuário expirou.
     *
     * <p><b>Assertiva de saída:</b> retorna {@code true}. Como o sistema atual não obriga
     * a rotação de senhas, as credenciais não expiram.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indica se o usuário está habilitado no sistema.
     *
     * <p><b>Assertiva de saída:</b> retorna {@code true}. Como o sistema atual não possui soft-delete
     * (inativação lógica), todos os registros persistidos são considerados habilitados.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}