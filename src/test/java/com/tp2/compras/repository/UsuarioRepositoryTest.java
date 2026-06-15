package com.tp2.compras.repository;

import com.tp2.compras.model.Papel;
import com.tp2.compras.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de repositório para {@link UsuarioRepository}.
 *
 * <p>Rastreamento de requisitos:
 * <ul>
 *   <li>EU001 - Eu como usuário quero poder criar uma conta no site para poder interagir.</li>
 *   <li>EU002 - Eu como usuário quero poder fazer login no site se já possuo uma conta e cair na tela principal.</li>
 * </ul>
 */
@DataJpaTest
@ActiveProfiles("test")
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario usuarioBase;

    @BeforeEach
    void setUp() {
        usuarioBase = Usuario.builder()
                .nome("Ana Silva")
                .email("ana@email.com")
                .senhaHash("$2a$10$hash_bcrypt_simulado")
                .papel(Papel.USUARIO)
                .build();
    }

    // --- EU001: Criar conta ---

    /** EU001 - Deve salvar novo usuário no banco com todos os campos obrigatórios. */
    @Test
    @DisplayName("EU001 - Deve salvar novo usuário no banco")
    void deveSalvarNovoUsuario() {
        Usuario salvo = usuarioRepository.save(usuarioBase);

        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getNome()).isEqualTo("Ana Silva");
        assertThat(salvo.getEmail()).isEqualTo("ana@email.com");
        assertThat(salvo.getPapel()).isEqualTo(Papel.USUARIO);
        assertThat(salvo.getCriadoEm()).isNotNull();
    }

    /** EU001 - Deve rejeitar cadastro com e-mail duplicado. */
    @Test
    @DisplayName("EU001 - Deve rejeitar e-mail duplicado")
    void deveRejeitarEmailDuplicado() {
        usuarioRepository.save(usuarioBase);

        Usuario duplicado = Usuario.builder()
                .nome("Outro Nome")
                .email("ana@email.com")
                .senhaHash("outro_hash")
                .build();

        assertThrows(Exception.class, () -> {
            usuarioRepository.saveAndFlush(duplicado);
        });
    }

    /** EU001 - Deve detectar e-mail já existente antes de concluir o cadastro. */
    @Test
    @DisplayName("EU001 - Deve verificar se e-mail já existe antes do cadastro")
    void deveDetectarEmailJaExistente() {
        usuarioRepository.save(usuarioBase);

        assertTrue(usuarioRepository.existsByEmail("ana@email.com"));
        assertFalse(usuarioRepository.existsByEmail("novo@email.com"));
    }

    // --- EU002: Login ---

    /** EU002 - Deve recuperar o usuário pelo e-mail para autenticação. */
    @Test
    @DisplayName("EU002 - Deve encontrar usuário pelo e-mail para autenticação")
    void deveEncontrarUsuarioPorEmail() {
        usuarioRepository.save(usuarioBase);

        Optional<Usuario> encontrado = usuarioRepository.findByEmail("ana@email.com");

        assertTrue(encontrado.isPresent());
        assertThat(encontrado.get().getNome()).isEqualTo("Ana Silva");
        assertThat(encontrado.get().getSenhaHash()).isNotBlank();
    }

    /** EU002 - Deve retornar vazio para e-mail não cadastrado (login inválido). */
    @Test
    @DisplayName("EU002 - Deve retornar vazio para e-mail não cadastrado")
    void deveRetornarVazioParaEmailNaoCadastrado() {
        Optional<Usuario> resultado = usuarioRepository.findByEmail("naoexiste@email.com");

        assertTrue(resultado.isEmpty());
    }

    /** EU001 - Deve salvar usuário com papel ADMINISTRADOR. */
    @Test
    @DisplayName("EU001 - Deve salvar usuário com papel ADMINISTRADOR")
    void deveSalvarAdministrador() {
        Usuario admin = Usuario.builder()
                .nome("Admin Sistema")
                .email("admin@compras.com")
                .senhaHash("$2a$10$hash_admin")
                .papel(Papel.ADMINISTRADOR)
                .build();

        Usuario salvo = usuarioRepository.save(admin);

        assertThat(salvo.getPapel()).isEqualTo(Papel.ADMINISTRADOR);
    }
}
