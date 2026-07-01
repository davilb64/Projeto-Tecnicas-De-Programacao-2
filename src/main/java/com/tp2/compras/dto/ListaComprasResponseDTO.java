package com.tp2.compras.dto;

import com.tp2.compras.model.ListaCompras;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record ListaComprasResponseDTO(
        Long id,
        String nome,
        LocalDateTime atualizadoEm,
        List<ItemListaResponseDTO> itens
) {
    public static ListaComprasResponseDTO daEntidade(ListaCompras lista) {
        return new ListaComprasResponseDTO(
                lista.getId(),
                lista.getNome(),
                lista.getAtualizadoEm(),
                lista.getItens().stream().map(ItemListaResponseDTO::daEntidade).collect(Collectors.toList())
        );
    }
}
