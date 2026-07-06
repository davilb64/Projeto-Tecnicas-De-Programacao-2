package com.tp2.compras.dto;

import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Entidade DTO para update de estabelecimento.
 **/
public record EstabelecimentoUpdateDTO(
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres")
        String nome,

        @Size(max = 255, message = "O endereço deve ter no máximo 255 caracteres")
        String endereco,

        BigDecimal latitude,
        BigDecimal longitude
) {}