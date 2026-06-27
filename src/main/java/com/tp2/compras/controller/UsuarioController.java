package com.tp2.compras.controller;

import com.tp2.compras.dto.UsuarioCadastroDTO;
import com.tp2.compras.dto.UsuarioLoginDTO;
import com.tp2.compras.model.Usuario;
import com.tp2.compras.service.UsuarioService;

import com.tp2.compras.infra.security.TokenService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*") // Libera o acesso para o frontend fazer requisições sem erro de CORS
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    /**
     * Endpoint para cadastro de novos usuários.
     * Mapeado para receber requisições POST na URL: /api/usuarios/cadastro
     */
    @PostMapping("/cadastro")
    public ResponseEntity<String> cadastrar(@Valid @RequestBody UsuarioCadastroDTO dto) {
        // A anotação @Valid obriga o Spring a checar as regras colocadas no DTO antes de entrar no método

        // Se der erro, lança a exceção personalizada
        Usuario usuarioCriado = usuarioService.cadastrar(dto);

        // Retorna o status HTTP 201 (Created) em caso de sucesso
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Usuário " + usuarioCriado.getNome() + " cadastrado com sucesso!");
    }

    /**
     * Endpoint para login de usuários (EU002).
     * Mapeado para receber requisições POST na URL: /api/usuarios/login
     *
     * <p><b>Assertiva de entrada:</b> dto contendo e-mail e senha não nulos.
     *
     * <p><b>Assertiva de saída:</b> retorna o token JWT em caso de credenciais válidas.
     *
     * <p><b>Argumentação da corretude:</b>
     * <ol>
     *   <li>O {@code UsernamePasswordAuthenticationToken} encapsula as credenciais brutas do DTO.</li>
     *   <li>O {@code authenticationManager.authenticate()} delega a verificação ao Spring Security,
     *       que busca o usuário pelo e-mail (UserDetailsService) e compara o hash da senha (PasswordEncoder).</li>
     *   <li>Se as credenciais forem inválidas, uma exceção de segurança é lançada e o fluxo é interrompido.</li>
     *   <li>Se forem válidas, o objeto de Autenticação é retornado contendo a entidade Usuario (Principal).</li>
     *   <li>O {@code tokenService} gera e assina o JWT com base na entidade autenticada, retornando a string ao cliente.</li>
     * </ol>
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody UsuarioLoginDTO dto) {

        // 1. Cria o token bruto de verificação com os dados que vieram do formulário
        var usernamePassword = new UsernamePasswordAuthenticationToken(dto.email(), dto.senha());

        // 2. Dispara o mecanismo de autenticação do Spring Security
        Authentication auth = this.authenticationManager.authenticate(usernamePassword);

        // 3. Recupera o usuário validado e gera o JWT
        Usuario usuarioValidado = (Usuario) auth.getPrincipal();
        String tokenJwt = tokenService.gerarToken(usuarioValidado);

        // 4. Retorna a chave gerada (o frontend fará o localStorage.setItem com ela)
        return ResponseEntity.ok(tokenJwt);
    }

    /**
     * Endpoint para manter servidor up
     */
    @GetMapping("/status")
    public ResponseEntity<String> status() {
        return ResponseEntity.ok("Online");
    }
}