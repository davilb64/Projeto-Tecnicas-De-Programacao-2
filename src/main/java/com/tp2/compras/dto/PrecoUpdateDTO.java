package com.tp2.compras.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * DTO para atualização de um registro de preço (ex: corrigir um erro de digitação).
 */
public record PrecoUpdateDTO(
        @NotNull(message = "O valor monetário é obrigatório")
        @DecimalMin(value = "0.01", message = "O valor deve ser de pelo menos R$ 0,01")
        BigDecimal valor
) {}