package com.tp2.compras.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record VariacaoProdutoCadastroDTO(
        @NotNull(message = "O ID do produto pai é obrigatório")
        Long produtoId,

        @NotBlank(message = "A descrição da variação é obrigatória")
        @Size(max = 100, message = "A descrição deve ter no máximo 100 caracteres")
        String descricao,

        BigDecimal peso,

        @Size(max = 20, message = "A unidade deve ter no máximo 20 caracteres")
        String unidade,

        @Size(max = 50, message = "O código de barras deve ter no máximo 50 caracteres")
        String codigoBarras
) {}