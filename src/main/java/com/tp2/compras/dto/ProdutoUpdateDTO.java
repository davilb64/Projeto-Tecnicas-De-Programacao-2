package com.tp2.compras.dto;

import jakarta.validation.constraints.Size;

public record ProdutoUpdateDTO(
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres")
        String nome,

        String descricao,

        @Size(max = 100, message = "A categoria deve ter no máximo 100 caracteres")
        String categoria
) {}