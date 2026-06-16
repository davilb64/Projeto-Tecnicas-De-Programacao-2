package com.tp2.compras.service;

import com.tp2.compras.dto.UsuarioCadastroDTO;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


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
        dtoValido = new UsuarioCadastroDTO("Davi", "davi@email.com", "senha123");
    }

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

    @Test
    @DisplayName("Deve lançar exceção ao tentar cadastrar e-mail já existente")
    void deveLancarExcecaoQuandoEmailJaExiste() {
        // Arrange: Forçamos o banco a dizer que o e-mail já existe
        when(usuarioRepository.existsByEmail(dtoValido.email())).thenReturn(true);

        // Act & Assert: Verifica se a regra de negócio dispara a exceção correta
        IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.cadastrar(dtoValido);
        });

        assertEquals("E-mail já cadastrado no sistema.", excecao.getMessage());
        
        // Garante que o banco NUNCA foi chamado para salvar
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
}
