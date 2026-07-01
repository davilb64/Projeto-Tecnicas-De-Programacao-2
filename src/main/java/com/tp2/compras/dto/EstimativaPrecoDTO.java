package com.tp2.compras.dto;

import java.math.BigDecimal;

public record EstimativaPrecoDTO(
        BigDecimal valor,
        String tipo // Pode ser "EXATO_MERCADO", "MEDIA_GERAL" ou "NENHUM"
) {}