package com.tp2.compras.dto;

import java.math.BigDecimal;

public record ComparacaoMercadoDTO(
        Long estabelecimentoId,
        String nomeEstabelecimento,
        BigDecimal valorTotal,
        int itensEncontrados,
        int totalItens,
        String mensagem,
        BigDecimal latitude,
        BigDecimal longitude
) {}