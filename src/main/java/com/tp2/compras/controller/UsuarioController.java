package com.tp2.compras.controller;

import com.tp2.compras.dto.UsuarioCadastroDTO;
import com.tp2.compras.dto.UsuarioLoginDTO;
import com.tp2.compras.dto.UsuarioResponseDTO;
import com.tp2.compras.dto.UsuarioUpdateDTO;
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
     * Endpoint para cadastro de novos usuários no sistema.
     * Recebe os dados de entrada, valida os formatos e repassa para a camada de serviço.
     *
     * <p>Rastreamento de requisitos:
     * <ul>
     * <li>EU001 - Eu como usuário quero poder criar uma conta no site para poder interagir.</li>
     * </ul>
     *
     * @param dto objeto envelopado contendo nome, email e senha enviados no corpo da requisição (POST).
     * @return ResponseEntity contendo a mensagem de sucesso e status HTTP 201 (Created).
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
     * Endpoint para buscar as informações de um usuário.
     * * <p>Rastreamento de requisitos:
     * <ul>
     * <li>EU001 - Eu como usuário/admin quero visualizar os dados da minha conta.</li>
     * </ul>
     */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarUsuario(@PathVariable Long id) {
        try {
            UsuarioResponseDTO response = usuarioService.buscarPorId(id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint para atualizar os dados de um usuário existente.
     * * <p>Rastreamento de requisitos:
     * <ul>
     * <li>EU001 - Eu como usuário/admin quero poder editar as informações da minha conta.</li>
     * </ul>
     */
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioUpdateDTO dto) {
        try {
            UsuarioResponseDTO response = usuarioService.atualizar(id, dto);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint para exclusão de uma conta de usuário.
     * * <p>Rastreamento de requisitos:
     * <ul>
     * <li>EU001 - Eu como usuário/admin quero poder deletar a minha conta do sistema.</li>
     * </ul>
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id) {
        try {
            usuarioService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint de infraestrutura (Health Check) para manter o servidor online.
     *
     * <p>Rastreamento de requisitos:
     * <ul>
     * <li>Requisito Não Funcional (RNF) - Manutenção de disponibilidade do servidor.</li>
     * </ul>
     * * @return ResponseEntity contendo a string "Online" e status HTTP 200 (OK).
     */
    @GetMapping("/status")
    public ResponseEntity<String> status() {
        return ResponseEntity.ok("Online");
    }
}