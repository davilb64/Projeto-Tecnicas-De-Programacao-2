package com.tp2.compras.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ListaComprasCadastroDTO(
        @NotNull(message = "O ID do usuário é obrigatório")
        Long usuarioId,

        @NotBlank(message = "O nome da lista é obrigatório")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String nome
) {}
