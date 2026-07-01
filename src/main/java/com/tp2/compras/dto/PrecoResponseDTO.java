package com.tp2.compras.dto;

import com.tp2.compras.model.Preco;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de resposta para exibição de preços.
 * Já entrega os dados formatados (nome do produto e do mercado) para facilitar a vida do Front-end.
 */
public record PrecoResponseDTO(
        Long id,
        Long variacaoId,
        String nomeProdutoVariacao,
        Long estabelecimentoId,
        String nomeEstabelecimento,
        Long usuarioId,
        String nomeUsuario,
        BigDecimal valor,
        LocalDateTime dataRegistro
) {
    public static PrecoResponseDTO daEntidade(Preco preco) {
        String nomeProdVar = preco.getVariacao().getProduto().getNome() + " - " + preco.getVariacao().getDescricao();

        return new PrecoResponseDTO(
                preco.getId(),
                preco.getVariacao().getId(),
                nomeProdVar,
                preco.getEstabelecimento().getId(),
                preco.getEstabelecimento().getNome(),
                preco.getRegistradoPor().getId(),
                preco.getRegistradoPor().getNome(),
                preco.getValor(),
                preco.getDataRegistro()
        );
    }
}