package com.tp2.compras.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.tp2.compras.model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    // Lê a chave secreta do application.properties
    @Value("${api.security.token.secret}")
    private String secret;

    /**
     * Gera um token JWT assinado para o usuário autenticado.
     *
     * <p><b>Assertiva de entrada:</b> usuario != null, contendo e-mail válido.
     * <p><b>Assertiva de saída:</b> retorna uma String com o JWT válido por 2 horas.
     */
    public String gerarToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("carrinho-coletivo-api")
                    .withSubject(usuario.getEmail()) // Guarda o email dentro do token
                    .withExpiresAt(gerarDataExpiracao()) // Duração do token
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    /**
     * Valida a assinatura e a expiração do token, extraindo o e-mail (subject).
     *
     * <p><b>Assertiva de entrada:</b> token (String).
     * <p><b>Assertiva de saída:</b> retorna o e-mail do usuário se válido; string vazia se inválido.
     */
    public String validarToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("carrinho-coletivo-api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            return ""; // Token forjado, expirado ou formato incorreto
        }
    }

    private Instant gerarDataExpiracao() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}