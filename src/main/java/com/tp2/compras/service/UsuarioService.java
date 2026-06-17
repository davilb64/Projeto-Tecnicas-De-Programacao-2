package com.tp2.compras.service;

import com.tp2.compras.dto.UsuarioCadastroDTO;
import com.tp2.compras.dto.UsuarioLoginDTO;
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
     * Realiza o cadastro de um novo usuário comum no sistema.
     * * Pré-condições (Assertivas de Entrada):
     * 1. dto não pode ser nulo.
     * 2. e-mail não pode estar previamente cadastrado no banco.
     * * Pós-condições (Assertivas de Saída):
     * 1. Retorna a entidade Usuario persistida com ID gerado.
     * 2. A senha é armazenada apenas em formato Hash irreversível.
     */
    @Transactional
    public Usuario cadastrar(UsuarioCadastroDTO dto) {
        // Assertiva estrutural de entrada
        Assert.notNull(dto, "O objeto de cadastro não pode ser nulo");

        // Regra de Negócio: Validação de duplicidade
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("E-mail já cadastrado no sistema.");
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
     * Realiza a autenticação do usuário para o Login.
     * Retorna true se sucesso, ou lança exceção se as credenciais estiverem erradas.
     */
    public boolean autenticar(UsuarioLoginDTO dto) {
        Assert.notNull(dto, "O objeto de login não pode ser nulo");

        // Busca o usuário no banco. Se não achar, lança o erro genérico de segurança.
        Usuario usuario = usuarioRepository.findByEmail(dto.email())
                .orElseThrow(() -> new IllegalArgumentException("Credenciais inválidas."));

        // Compara a senha digitada em texto puro com o Hash salvo no banco
        boolean senhaCorreta = passwordEncoder.matches(dto.senha(), usuario.getSenhaHash());

        if (!senhaCorreta) {
            throw new IllegalArgumentException("Credenciais inválidas.");
        }

        return true;
    }
}