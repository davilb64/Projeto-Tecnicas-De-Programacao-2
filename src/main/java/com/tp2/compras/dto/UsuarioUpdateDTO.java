package com.tp2.compras.dto;

import jakarta.validation.constraints.Size;

/**
 * Objeto de Transferência de Dados (DTO) para atualização de perfil do usuário.
 *
 * <p>Rastreamento de requisitos:
 * <ul>
 * <li>EU001 - Eu como usuário/admin quero poder editar as informações da minha conta.</li>
 * </ul>
 */
public record UsuarioUpdateDTO(
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String nome,

        @Size(min = 8, message = "A nova senha deve conter no mínimo 8 caracteres")
        String senha
) {}