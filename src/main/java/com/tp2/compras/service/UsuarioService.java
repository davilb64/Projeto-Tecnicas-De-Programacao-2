package com.tp2.compras.service;

import com.tp2.compras.dto.UsuarioCadastroDTO;
import com.tp2.compras.dto.UsuarioLoginDTO;
import com.tp2.compras.dto.UsuarioResponseDTO;
import com.tp2.compras.dto.UsuarioUpdateDTO;
import com.tp2.compras.exception.CredenciaisInvalidasException;
import com.tp2.compras.exception.EmailJaCadastradoException;
import com.tp2.compras.model.Papel;
import com.tp2.compras.model.Usuario;
import com.tp2.compras.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    // O RequiredArgsConstructor do Lombok injeta as dependências automaticamente
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Realiza o cadastro de um novo usuário comum no sistema garantindo a criptografia dos dados de acesso.
     *
     * <p>Rastreamento de requisitos:
     * <ul>
     * <li>EU001 - Eu como usuário quero poder criar uma conta no site para poder interagir.</li>
     * </ul>
     *
     * <p>Pré-condições (Assertivas de Entrada):
     * <ul>
     * <li>O objeto {@code dto} não pode ser nulo.</li>
     * <li>O e-mail informado não pode estar previamente cadastrado no banco de dados.</li>
     * </ul>
     *
     * <p>Pós-condições (Assertivas de Saída):
     * <ul>
     * <li>Retorna a entidade {@link Usuario} devidamente persistida com a alocação de ID gerado.</li>
     * <li>A senha em texto plano é destruída e armazenada apenas em formato Hash irreversível (BCrypt).</li>
     * <li>O papel de acesso (Role) é definido estritamente como {@code Papel.USUARIO} por padrão estrutural.</li>
     * </ul>
     *
     * @param dto objeto de transferência contendo os dados brutos de cadastro (nome, e-mail e senha).
     * @return Entidade {@code Usuario} recém-criada e gravada no banco de dados.
     * @throws EmailJaCadastradoException se houver violação da regra de negócio estrutural (e-mail duplicado).
    */
    @Transactional
    public Usuario cadastrar(UsuarioCadastroDTO dto) {
        // Assertiva estrutural de entrada
        Assert.notNull(dto, "O objeto de cadastro não pode ser nulo");

        // Regra de Negócio: Validação de duplicidade
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new EmailJaCadastradoException("E-mail já cadastrado no sistema.");
        }

        // Criptografia da senha usando BCrypt
        String hash = passwordEncoder.encode(dto.senha());

        // Mapeamento do DTO para a Entidade do Banco (usando o padrão Builder)
        Usuario novoUsuario = Usuario.builder()
                .nome(dto.nome())
                .email(dto.email())
                .senhaHash(hash)
                .papel(Papel.USUARIO) // Inicia com o menor privilégio por segurança
                .build();

        // Persistência
        return usuarioRepository.save(novoUsuario);
    }

    /**
     * Realiza a autenticação do usuário para o Login verificando as credenciais.
     *
     * <p>Rastreamento de requisitos:
     * <ul>
     * <li>EU002 - Eu como usuário quero poder fazer login no site se já possuo uma conta.</li>
     * </ul>
     *
     * <p>Pré-condições (Assertivas de Entrada):
     * <ul>
     * <li>O objeto {@code dto} recebido não pode ser nulo.</li>
     * </ul>
     *
     * <p>Pós-condições (Assertivas de Saída):
     * <ul>
     * <li>Retorna {@code true} exclusivamente se as credenciais forem válidas e a comparação matemática do hash corresponder.</li>
     * </ul>
     *
     * @param dto objeto envelopado contendo as credenciais de acesso (e-mail e senha) fornecidas pelo usuário.
     * @return {@code true} caso a autenticação no banco seja bem-sucedida.
     * @throws CredenciaisInvalidasException se o e-mail não for encontrado ou se a senha estiver incorreta.
    */
    public boolean autenticar(UsuarioLoginDTO dto) {
        Assert.notNull(dto, "O objeto de login não pode ser nulo");

        // Busca o usuário no banco. Se não achar, lança o erro genérico de segurança.
        Usuario usuario = usuarioRepository.findByEmail(dto.email())
                .orElseThrow(() -> new CredenciaisInvalidasException("Credenciais inválidas."));

        // Compara a senha digitada em texto puro com o Hash salvo no banco
        boolean senhaCorreta = passwordEncoder.matches(dto.senha(), usuario.getSenhaHash());

        if (!senhaCorreta) {
            throw new CredenciaisInvalidasException("Credenciais inválidas.");
        }

        return true;
    }

    /**
     * Busca um usuário específico pelo seu identificador único.
     *
     * <p>Rastreamento de requisitos:
     * <ul>
     * <li>EU001 - Eu como usuário/admin quero visualizar os dados da minha conta.</li>
     * </ul>
     *
     * @param id identificador do usuário no banco de dados.
     * @return UsuarioResponseDTO contendo os dados seguros do usuário.
     */
    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
        
        return UsuarioResponseDTO.daEntidade(usuario);
    }

    /**
     * Atualiza os dados cadastrais permitidos de um usuário.
     *
     * <p>Rastreamento de requisitos:
     * <ul>
     * <li>EU001 - Eu como usuário/admin quero poder editar as informações da minha conta.</li>
     * </ul>
     *
     * @param id identificador do usuário a ser atualizado.
     * @param dto objeto contendo os novos dados preenchidos.
     * @return UsuarioResponseDTO com o estado atualizado do usuário.
     */
    @Transactional
    public UsuarioResponseDTO atualizar(Long id, UsuarioUpdateDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        // Se o nome foi enviado e não está vazio, atualiza
        if (dto.nome() != null && !dto.nome().isBlank()) {
            usuario.setNome(dto.nome());
        }
        
        // Se a senha foi enviada, fazemos o hash antes de salvar
        if (dto.senha() != null && !dto.senha().isBlank()) {
            usuario.setSenhaHash(passwordEncoder.encode(dto.senha()));
        }

        return UsuarioResponseDTO.daEntidade(usuarioRepository.save(usuario));
    }

    /**
     * Remove fisicamente um usuário do sistema.
     *
     * <p>Rastreamento de requisitos:
     * <ul>
     * <li>EU001 - Eu como usuário/admin quero poder deletar a minha conta do sistema.</li>
     * </ul>
     *
     * @param id identificador do usuário a ser removido.
     */
    @Transactional
    public void deletar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuário não encontrado para deleção.");
        }
        usuarioRepository.deleteById(id);
    }
}