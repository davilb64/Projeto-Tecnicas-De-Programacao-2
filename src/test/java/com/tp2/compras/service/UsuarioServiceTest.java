package com.tp2.compras.service;

import com.tp2.compras.dto.UsuarioCadastroDTO;
import com.tp2.compras.dto.UsuarioResponseDTO;
import com.tp2.compras.dto.UsuarioUpdateDTO;
import com.tp2.compras.model.Usuario;
import com.tp2.compras.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import com.tp2.compras.exception.EmailJaCadastradoException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Testes de unidade para a camada de serviço {@link UsuarioService}.
 *
 * <p>Rastreamento de requisitos:
 * <ul>
 * <li>EU001 - Eu como usuário quero poder criar uma conta no site para poder interagir.</li>
 * <li>EU001 - Eu como usuário/admin quero visualizar, editar e deletar minha conta (CRUD).</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository; // Falsifica o banco de dados

    @Mock
    private PasswordEncoder passwordEncoder; // Falsifica o gerador de hash

    @InjectMocks
    private UsuarioService usuarioService; // A classe real que estamos testando

    private UsuarioCadastroDTO dtoValido;

    @BeforeEach
    void setUp() {
        // Estado inicial dos testes
        dtoValido = new UsuarioCadastroDTO("Romulo", "romulo@gmail.com", "senha123");
    }

    /** EU001 - Deve cadastrar um usuário com sucesso e aplicar o hash na senha. */
    @Test
    @DisplayName("Deve cadastrar usuário com sucesso quando os dados forem válidos")
    void deveCadastrarUsuarioComSucesso() {
        // Configuração do ambiente (Arrange)
        when(usuarioRepository.existsByEmail(dtoValido.email())).thenReturn(false);
        when(passwordEncoder.encode(dtoValido.senha())).thenReturn("$2a$10$hash_falso_gerado");
        
        Usuario usuarioMockSalvo = Usuario.builder()
                .id(1L)
                .nome(dtoValido.nome())
                .email(dtoValido.email())
                .senhaHash("$2a$10$hash_falso_gerado")
                .build();
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioMockSalvo);

        // Execução (Act)
        Usuario resultado = usuarioService.cadastrar(dtoValido);

        // Verificação por Assertivas (Assert)
        assertNotNull(resultado.getId(), "O ID do usuário salvo não deveria ser nulo");
        assertEquals(dtoValido.nome(), resultado.getNome());
        assertEquals("$2a$10$hash_falso_gerado", resultado.getSenhaHash());
        
        // Garante que o método save do banco foi chamado exatamente 1 vez
        verify(usuarioRepository, times(1)).save(any(Usuario.class)); 
    }

    /** EU001 - Deve rejeitar o cadastro e lançar exceção de negócio se o e-mail já estiver em uso. */
    @Test
    @DisplayName("Deve lançar exceção ao tentar cadastrar e-mail já existente")
    void deveLancarExcecaoQuandoEmailJaExiste() {
        // Arrange: Forçamos o banco a dizer que o e-mail já existe
        when(usuarioRepository.existsByEmail(dtoValido.email())).thenReturn(true);

        // Act & Assert: Verifica se a regra de negócio dispara a exceção correta (Customizada)
        EmailJaCadastradoException excecao = assertThrows(EmailJaCadastradoException.class, () -> {
            usuarioService.cadastrar(dtoValido);
        });

        assertEquals("E-mail já cadastrado no sistema.", excecao.getMessage());
        
        // Garante que o banco NUNCA foi chamado para salvar
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    /** EU001 - Deve buscar um usuário pelo ID e retorná-lo em um DTO seguro de resposta. */
    @Test
    @DisplayName("Deve buscar utilizador por ID com sucesso")
    void deveBuscarUsuarioPorIdComSucesso() {
        // Arrange
        Usuario usuarioMock = Usuario.builder().id(1L).nome("Rômulo").email("romulo@unb.br").build();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));

        // Act
        UsuarioResponseDTO resultado = usuarioService.buscarPorId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals("Rômulo", resultado.nome());
        assertEquals("romulo@unb.br", resultado.email());
        verify(usuarioRepository, times(1)).findById(1L);
    }

    /** EU001 - Deve lançar exceção caso o sistema tente buscar um ID que não existe. */
    @Test
    @DisplayName("Deve lançar exceção ao buscar ID inexistente")
    void deveLancarExcecaoAoBuscarIdInexistente() {
        // Arrange
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.buscarPorId(99L);
        });
        assertEquals("Usuário não encontrado.", excecao.getMessage());
    }

    /** EU001 - Deve permitir a atualização dos dados e criptografar a nova senha. */
    @Test
    @DisplayName("Deve atualizar o nome e a senha do utilizador com sucesso")
    void deveAtualizarUsuarioComSucesso() {
        // Arrange
        Usuario usuarioMock = Usuario.builder().id(1L).nome("Rômulo Antigo").senhaHash("hash_antigo").build();
        UsuarioUpdateDTO updateDTO = new UsuarioUpdateDTO("Rômulo Novo", "senhaNova123");
        
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        when(passwordEncoder.encode(updateDTO.senha())).thenReturn("$2a$10$hash_novo_gerado");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioMock); // O mock do save retorna o próprio objeto alterado

        // Act
        UsuarioResponseDTO resultado = usuarioService.atualizar(1L, updateDTO);

        // Assert
        assertEquals("Rômulo Novo", resultado.nome()); // Garante que o nome mudou
        assertEquals("$2a$10$hash_novo_gerado", usuarioMock.getSenhaHash()); // Garante que o hash mudou na entidade
        verify(usuarioRepository, times(1)).save(usuarioMock);
    }

    /** EU001 - Deve deletar fisicamente o usuário do banco se ele existir. */
    @Test
    @DisplayName("Deve deletar utilizador com sucesso")
    void deveDeletarUsuarioComSucesso() {
        // Arrange
        when(usuarioRepository.existsById(1L)).thenReturn(true);

        // Act
        usuarioService.deletar(1L);

        // Assert
        verify(usuarioRepository, times(1)).deleteById(1L); // Verifica se o comando de deletar foi chamado no banco
    }

    /** EU001 - Deve bloquear a deleção e lançar exceção caso o ID do usuário não exista. */
    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar ID inexistente")
    void deveLancarExcecaoAoDeletarIdInexistente() {
        // Arrange
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.deletar(99L);
        });
        assertEquals("Usuário não encontrado para deleção.", excecao.getMessage());
        verify(usuarioRepository, never()).deleteById(anyLong()); // Garante que NUNCA chamou o delete
    }
}
