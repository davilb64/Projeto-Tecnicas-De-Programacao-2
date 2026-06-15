package com.tp2.compras.repository;

import com.tp2.compras.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório de acesso a dados para a entidade {@link Usuario}.
 *
 * <p>Rastreamento de requisitos:
 * <ul>
 *   <li>EU001 - Eu como usuário quero poder criar uma conta no site para poder interagir.</li>
 *   <li>EU002 - Eu como usuário quero poder fazer login no site se já possuo uma conta e cair na tela principal.</li>
 * </ul>
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca um usuário pelo e-mail para autenticação no login.
     *
     * <p>Assertiva de entrada: email não nulo.
     * <p>Assertiva de saída: retorna Optional com o usuário se encontrado, Optional.empty() caso contrário.
     *
     * @param email endereço de e-mail a ser pesquisado
     * @return Optional contendo o usuário, ou vazio se não encontrado (EU002)
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Verifica se já existe um usuário cadastrado com o e-mail informado.
     *
     * <p>Assertiva de entrada: email não nulo.
     * <p>Assertiva de saída: retorna true se o e-mail já existe, false caso contrário.
     *
     * @param email endereço de e-mail a verificar
     * @return true se o e-mail já está cadastrado (EU001)
     */
    boolean existsByEmail(String email);
}
