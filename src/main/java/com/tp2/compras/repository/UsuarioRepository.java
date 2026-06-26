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
     * <p><b>Assertiva de entrada:</b> email != null.
     *
     * <p><b>Assertiva de saída:</b> retorna Optional não-nulo; contém o usuário
     * correspondente se encontrado, ou Optional.empty() caso contrário.
     *
     * <p><b>Argumentação da corretude:</b>
     * <ol>
     *   <li>Spring Data JPA traduz este método em {@code SELECT * FROM usuarios WHERE email = ?},
     *       passando o parâmetro de forma segura via prepared statement — sem risco de SQL injection.</li>
     *   <li>A coluna email possui restrição UNIQUE no banco (definida em V1__create_usuarios_table.sql),
     *       portanto a query retorna no máximo um registro.</li>
     *   <li>Spring Data JPA envolve o resultado em {@code Optional}: se nenhum registro for encontrado,
     *       retorna {@code Optional.empty()}; se encontrado, retorna {@code Optional.of(usuario)}.
     *       Em nenhum caso o retorno é nulo, satisfazendo a assertiva de saída.</li>
     *   <li>Se email for nulo (violando a assertiva de entrada), o banco lançará exceção antes
     *       de qualquer dado ser retornado — o contrato da função não é violado.</li>
     * </ol>
     *
     * @param email endereço de e-mail a ser pesquisado (EU002)
     * @return Optional contendo o usuário, ou vazio se não encontrado
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Verifica se já existe um usuário cadastrado com o e-mail informado.
     *
     * <p><b>Assertiva de entrada:</b> email != null.
     *
     * <p><b>Assertiva de saída:</b> retorna true se e somente se existir exatamente
     * um registro na tabela usuarios com o email informado; false caso contrário.
     *
     * <p><b>Argumentação da corretude:</b>
     * <ol>
     *   <li>Spring Data JPA traduz este método em {@code SELECT COUNT(*) > 0 FROM usuarios WHERE email = ?},
     *       executado via prepared statement.</li>
     *   <li>A restrição UNIQUE na coluna email garante que COUNT retorna 0 ou 1 — nunca mais.
     *       Portanto o resultado booleano é determinístico e correto.</li>
     *   <li>O retorno {@code true} implica exatamente que já existe um cadastro com aquele e-mail,
     *       o que é a semântica esperada para a validação de duplicidade no fluxo de cadastro (EU001).</li>
     *   <li>O retorno {@code false} implica que o e-mail está disponível para cadastro,
     *       satisfazendo a assertiva de saída em ambos os casos.</li>
     * </ol>
     *
     * @param email endereço de e-mail a verificar (EU001)
     * @return true se o e-mail já está cadastrado, false caso contrário
     */
    boolean existsByEmail(String email);
}
