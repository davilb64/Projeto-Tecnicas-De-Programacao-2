package com.tp2.compras.dto;

import com.tp2.compras.model.Produto;
import com.tp2.compras.model.StatusProduto;

import java.time.LocalDateTime;

public record ProdutoResponseDTO(
        Long id,
        String nome,
        String codigoBarras,
        String descricao,
        String categoria,
        StatusProduto status,
        LocalDateTime criadoEm
) {
    public static ProdutoResponseDTO daEntidade(Produto produto) {
        return new ProdutoResponseDTO(
                produto.getId(),
                produto.getNome(),
                produto.getCodigoBarras(),
                produto.getDescricao(),
                produto.getCategoria(),
                produto.getStatus(),
                produto.getCriadoEm()
        );
    }
}