package com.tp2.compras.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tp2.compras.dto.UsuarioCadastroDTO;
import com.tp2.compras.dto.UsuarioLoginDTO;
import com.tp2.compras.infra.security.SecurityFilter;
import com.tp2.compras.infra.security.TokenService;
import com.tp2.compras.model.Papel;
import com.tp2.compras.model.Usuario;
import com.tp2.compras.service.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = UsuarioController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityFilter.class)
)
@AutoConfigureMockMvc(addFilters = false)
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private TokenService tokenService;

    @Test
    @DisplayName("Deve retornar HTTP 201 (Created) ao enviar um JSON de cadastro válido")
    void deveRetornar201AoCadastrarComSucesso() throws Exception {
        UsuarioCadastroDTO dtoValido = new UsuarioCadastroDTO("Fulano", "Fulano@unb.br", "senhaSegura123");
        Usuario usuarioSimulado = Usuario.builder().nome("Fulano").build();

        when(usuarioService.cadastrar(any(UsuarioCadastroDTO.class))).thenReturn(usuarioSimulado);

        mockMvc.perform(post("/api/usuarios/cadastro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoValido)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Usuário Fulano cadastrado com sucesso!"));
    }

    @Test
    @DisplayName("Deve retornar HTTP 400 (Bad Request) quando o DTO falhar na validação (e-mail inválido)")
    void deveRetornar400QuandoDadosInvalidos() throws Exception {
        UsuarioCadastroDTO dtoInvalido = new UsuarioCadastroDTO("Fulano", "email-errado", "123");

        mockMvc.perform(post("/api/usuarios/cadastro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar HTTP 400 quando o serviço lançar exceção de e-mail duplicado")
    void deveRetornar400QuandoEmailJaExistir() throws Exception {
        UsuarioCadastroDTO dtoDuplicado = new UsuarioCadastroDTO("Fulano", "duplicado@unb.br", "senha123");

        when(usuarioService.cadastrar(any(UsuarioCadastroDTO.class)))
                .thenThrow(new IllegalArgumentException("E-mail já cadastrado no sistema."));

        mockMvc.perform(post("/api/usuarios/cadastro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoDuplicado)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("E-mail já cadastrado no sistema."));
    }

    @Test
    @DisplayName("Deve retornar HTTP 200 (OK) com token JWT ao fazer login com credenciais corretas")
    void deveRetornar200AoLogarComSucesso() throws Exception {
        UsuarioLoginDTO dtoValido = new UsuarioLoginDTO("fulano@unb.br", "senhaSegura123");

        Usuario usuarioMock = Usuario.builder()
                .id(1L)
                .nome("Fulano")
                .email("fulano@unb.br")
                .papel(Papel.USUARIO)
                .build();

        var authToken = new UsernamePasswordAuthenticationToken(usuarioMock, null, usuarioMock.getAuthorities());

        when(authenticationManager.authenticate(any())).thenReturn(authToken);
        when(tokenService.gerarToken(any(Usuario.class))).thenReturn("token-jwt-fake");

        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoValido)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-jwt-fake"))
                .andExpect(jsonPath("$.email").value("fulano@unb.br"));
    }

    @Test
    @DisplayName("Deve retornar HTTP 401 (Unauthorized) quando a senha estiver errada ou email não existir")
    void deveRetornar401QuandoCredenciaisInvalidas() throws Exception {
        UsuarioLoginDTO dtoInvalido = new UsuarioLoginDTO("fulano@unb.br", "senhaErrada");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Credenciais inválidas."));

        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Credenciais inválidas."));
    }

    @Test
    @DisplayName("Deve retornar HTTP 400 (Bad Request) quando o DTO de login falhar na validação")
    void deveRetornar400QuandoLoginForInvalido() throws Exception {
        UsuarioLoginDTO dtoInvalido = new UsuarioLoginDTO("email-sem-arroba", "123");

        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest());
    }
}
