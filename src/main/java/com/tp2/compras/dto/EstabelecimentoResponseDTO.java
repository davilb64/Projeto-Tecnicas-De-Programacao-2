package com.tp2.compras.dto;

import com.tp2.compras.model.Estabelecimento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EstabelecimentoResponseDTO(
        Long id,
        String nome,
        String endereco,
        BigDecimal latitude,
        BigDecimal longitude,
        LocalDateTime criadoEm
) {
    public static EstabelecimentoResponseDTO daEntidade(Estabelecimento estabelecimento) {
        return new EstabelecimentoResponseDTO(
                estabelecimento.getId(),
                estabelecimento.getNome(),
                estabelecimento.getEndereco(),
                estabelecimento.getLatitude(),
                estabelecimento.getLongitude(),
                estabelecimento.getCriadoEm()
        );
    }
}
