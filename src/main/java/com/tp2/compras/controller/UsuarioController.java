package com.tp2.compras.controller;

import com.tp2.compras.dto.UsuarioCadastroDTO;
import com.tp2.compras.model.Usuario;
import com.tp2.compras.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    // Injeta a camada de serviço que acabamos de testar e construir
    private final UsuarioService usuarioService;

    /**
     * Endpoint para cadastro de novos usuários.
     * Mapeado para receber requisições POST na URL: /api/usuarios/cadastro
     */
    @PostMapping("/cadastro")
    public ResponseEntity<String> cadastrar(@Valid @RequestBody UsuarioCadastroDTO dto) {
        // A anotação @Valid obriga o Spring a checar aquelas regras que você colocou no DTO
        // (@NotBlank, @Email) antes mesmo de entrar neste método.

        try {
            // Repassa para o serviço processar
            Usuario usuarioCriado = usuarioService.cadastrar(dto);
            
            // Retorna o status HTTP 201 (Created) em caso de sucesso
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Usuário " + usuarioCriado.getNome() + " cadastrado com sucesso!");

        } catch (IllegalArgumentException e) {
            // Se o e-mail já existir, o serviço lança esta exceção, e nós retornamos HTTP 400 (Bad Request)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}