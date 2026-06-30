package com.tp2.compras.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ItemListaAdicionarDTO(
        @NotNull(message = "O ID da variação do produto é obrigatório")
        Long variacaoId,

        @Min(value = 1, message = "A quantidade mínima é 1")
        Integer quantidade
) {}
