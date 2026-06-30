package com.tp2.compras.dto;

import com.tp2.compras.model.VariacaoProduto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record VariacaoProdutoResponseDTO(
        Long id,
        Long produtoId,
        String descricao,
        BigDecimal peso,
        String unidade,
        String codigoBarras,
        LocalDateTime criadoEm
) {
    public static VariacaoProdutoResponseDTO daEntidade(VariacaoProduto variacao) {
        return new VariacaoProdutoResponseDTO(
                variacao.getId(),
                variacao.getProduto().getId(),
                variacao.getDescricao(),
                variacao.getPeso(),
                variacao.getUnidade(),
                variacao.getCodigoBarras(),
                variacao.getCriadoEm()
        );
    }
}