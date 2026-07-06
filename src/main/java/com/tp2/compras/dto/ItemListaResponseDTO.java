package com.tp2.compras.dto;

import com.tp2.compras.model.ItemLista;

/**
 * DTO de resposta para ItemLista.
 */
public record ItemListaResponseDTO(
        Long id,
        Long variacaoId,
        String nomeProduto,
        String descricaoVariacao,
        Integer quantidade,
        Boolean comprado
) {
  /**
   * Classe construtora DTO ItemListaResponse.
   */
  public static ItemListaResponseDTO daEntidade(ItemLista item) {
    return new ItemListaResponseDTO(
                item.getId(),
                item.getVariacao().getId(),
                item.getVariacao().getProduto().getNome(),
                item.getVariacao().getDescricao(),
                item.getQuantidade(),
                item.getComprado()
        );
  }
}