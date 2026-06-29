package com.tp2.compras.dto;

import com.tp2.compras.model.Papel;
import com.tp2.compras.model.Usuario;
import java.time.LocalDateTime;


/**
 * Objeto de Transferência de Dados (DTO) para envio seguro de informações do usuário.
 * Filtra dados sensíveis como a senha criptografada.
 *
 * <p>Rastreamento de requisitos:
 * <ul>
 * <li>EU001 - Eu como usuário/admin quero visualizar os dados da minha conta.</li>
 * </ul>
 */
public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        Papel papel,
        LocalDateTime criadoEm
) {
    public static UsuarioResponseDTO daEntidade(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPapel(),
                usuario.getCriadoEm()
        );
    }
}
