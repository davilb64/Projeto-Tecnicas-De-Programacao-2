package com.tp2.compras.dto;

import java.math.BigDecimal;

/**
 * Data Transfer Object (DTO).
 * Objeto simples para comparação dos mercados
 **/
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