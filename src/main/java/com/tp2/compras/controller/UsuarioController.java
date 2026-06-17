package com.tp2.compras.controller;

import com.tp2.compras.dto.UsuarioCadastroDTO;
import com.tp2.compras.dto.UsuarioLoginDTO;
import com.tp2.compras.model.Usuario;
import com.tp2.compras.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*") // Libera o acesso para o frontend fazer requisições sem erro de CORS
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    // Injeta a camada de serviço onde ficam as regras de negócio
    private final UsuarioService usuarioService;

    /**
     * Endpoint para cadastro de novos usuários.
     * Mapeado para receber requisições POST na URL: /api/usuarios/cadastro
     */
    @PostMapping("/cadastro")
    public ResponseEntity<String> cadastrar(@Valid @RequestBody UsuarioCadastroDTO dto) {
        // A anotação @Valid obriga o Spring a checar as regras colocadas no DTO antes de entrar no método

        // Se der erro, a nossa lança a exceção personalizada
        Usuario usuarioCriado = usuarioService.cadastrar(dto);

        // Retorna o status HTTP 201 (Created) em caso de sucesso
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Usuário " + usuarioCriado.getNome() + " cadastrado com sucesso!");
    }

    /**
     * Endpoint para login de usuários.
     * Mapeado para receber requisições POST na URL: /api/usuarios/login
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody UsuarioLoginDTO dto) {
        // Manda a exceção personalizada se email ou senha errados
        usuarioService.autenticar(dto);

        // Se passar direto pela autenticação sem lançar exceção, retorna 200 OK
        return ResponseEntity.ok("Login realizado com sucesso!");
    }
}