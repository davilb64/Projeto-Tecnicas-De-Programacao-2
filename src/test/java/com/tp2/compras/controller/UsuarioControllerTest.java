package com.tp2.compras.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tp2.compras.dto.UsuarioCadastroDTO;
import com.tp2.compras.dto.UsuarioLoginDTO;
import com.tp2.compras.model.Usuario;
import com.tp2.compras.service.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = UsuarioController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class} // Agora o Java sabe quem é essa classe
)
@AutoConfigureMockMvc(addFilters = false)
public class UsuarioControllerTest {
    
    @Autowired
    private MockMvc mockMvc; // O nosso simulador de requisições HTTP (como se fosse um Postman embutido)

    @Autowired
    private ObjectMapper objectMapper; // Ferramenta para converter objetos Java (DTO) em texto JSON

    // @MockBean cria uma versão falsa do nosso serviço para injetar na Controladora
    @MockBean
    private UsuarioService usuarioService;

    @Test
    @DisplayName("Deve retornar HTTP 201 (Created) ao enviar um JSON de cadastro válido")
    void deveRetornar201AoCadastrarComSucesso() throws Exception {
        // Arrange: Preparamos o DTO e o que o serviço falso deve responder
        UsuarioCadastroDTO dtoValido = new UsuarioCadastroDTO("Fulano", "Fulano@unb.br", "senhaSegura123");
        Usuario usuarioSimulado = Usuario.builder().nome("Fulano").build();
        
        when(usuarioService.cadastrar(any(UsuarioCadastroDTO.class))).thenReturn(usuarioSimulado);

        // Act & Assert: Simulamos o envio de um POST para a URL e verificamos a resposta
        mockMvc.perform(post("/api/usuarios/cadastro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoValido))) // Converte o DTO para '{"nome":"Fulano"...}'
                .andExpect(status().isCreated()) // Espera o status 201
                .andExpect(content().string("Usuário Fulano cadastrado com sucesso!"));
    }

    @Test
    @DisplayName("Deve retornar HTTP 400 (Bad Request) quando o DTO falhar na validação (e-mail inválido)")
    void deveRetornar400QuandoDadosInvalidos() throws Exception {
        // Arrange: DTO com e-mail fora do formato e senha curta
        UsuarioCadastroDTO dtoInvalido = new UsuarioCadastroDTO("Fulano", "email-errado", "123");

        // Act & Assert
        mockMvc.perform(post("/api/usuarios/cadastro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest()); // O @Valid do Controller deve intercetar e barrar
    }

    @Test
    @DisplayName("Deve retornar HTTP 400 quando o serviço lançar exceção de e-mail duplicado")
    void deveRetornar400QuandoEmailJaExistir() throws Exception {
        // Arrange: DTO válido, mas simulamos o serviço a rejeitar
        UsuarioCadastroDTO dtoDuplicado = new UsuarioCadastroDTO("Fulano", "duplicado@unb.br", "senha123");
        
        when(usuarioService.cadastrar(any(UsuarioCadastroDTO.class)))
                .thenThrow(new IllegalArgumentException("E-mail já cadastrado no sistema."));

        // Act & Assert
        mockMvc.perform(post("/api/usuarios/cadastro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoDuplicado)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("E-mail já cadastrado no sistema."));
    }

    // =========================================================
    // TESTES DE LOGIN (TDD)
    // =========================================================

    @Test
    @DisplayName("Deve retornar HTTP 200 (OK) ao fazer login com credenciais corretas")
    void deveRetornar200AoLogarComSucesso() throws Exception {
        // Arrange: Preparamos o DTO de login e ensinamos o mock a retornar 'true'
        UsuarioLoginDTO dtoValido = new UsuarioLoginDTO("fulano@unb.br", "senhaSegura123");

        when(usuarioService.autenticar(any(UsuarioLoginDTO.class))).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoValido)))
                .andExpect(status().isOk())
                .andExpect(content().string("Login realizado com sucesso!"));
    }

    @Test
    @DisplayName("Deve retornar HTTP 401 (Unauthorized) quando a senha estiver errada ou email não existir")
    void deveRetornar401QuandoCredenciaisInvalidas() throws Exception {
        // Arrange: DTO com dados errados. Ensinamos o mock a lançar exceção.
        UsuarioLoginDTO dtoInvalido = new UsuarioLoginDTO("fulano@unb.br", "senhaErrada");

        when(usuarioService.autenticar(any(UsuarioLoginDTO.class)))
                .thenThrow(new IllegalArgumentException("Credenciais inválidas."));

        // Act & Assert
        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isUnauthorized()) // Status 401
                .andExpect(content().string("Credenciais inválidas."));
    }

    @Test
    @DisplayName("Deve retornar HTTP 400 (Bad Request) quando o DTO de login falhar na validação")
    void deveRetornar400QuandoLoginForInvalido() throws Exception {
        // Arrange: DTO com e-mail inválido e senha muito curta (menor que 8 caracteres)
        UsuarioLoginDTO dtoInvalido = new UsuarioLoginDTO("email-sem-arroba", "123");

        // Act & Assert: O @Valid do Controller deve bloquear antes mesmo de chamar o Service
        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest());
    }
}


