package com.tp2.compras.dto;

import com.tp2.compras.model.ItemLista;

public record ItemListaResponseDTO(
        Long id,
        Long variacaoId,
        String descricaoVariacao,
        Integer quantidade,
        Boolean comprado
) {
    public static ItemListaResponseDTO daEntidade(ItemLista item) {
        return new ItemListaResponseDTO(
                item.getId(),
                item.getVariacao().getId(),
                item.getVariacao().getDescricao(),
                item.getQuantidade(),
                item.getComprado()
        );
    }
}
