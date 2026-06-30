package com.tp2.compras.dto;

import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record VariacaoProdutoUpdateDTO(
        @Size(max = 100, message = "A descrição deve ter no máximo 100 caracteres")
        String descricao,

        BigDecimal peso,

        @Size(max = 20, message = "A unidade deve ter no máximo 20 caracteres")
        String unidade
) {}