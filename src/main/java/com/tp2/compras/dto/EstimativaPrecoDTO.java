package com.tp2.compras.dto;

import java.math.BigDecimal;

/**
 * Entidade DTO para estimativa de preço.
 **/
public record EstimativaPrecoDTO(
        BigDecimal valor,
        String tipo // Pode ser "EXATO_MERCADO", "MEDIA_GERAL" ou "NENHUM"
) {}