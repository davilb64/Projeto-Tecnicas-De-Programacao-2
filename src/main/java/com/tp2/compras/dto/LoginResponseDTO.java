package com.tp2.compras.dto;

import com.tp2.compras.model.Papel;

// DTO para devolver o Token e as informações básicas do usuário logado
public record LoginResponseDTO(
        String token,
        String email,
        Papel papel,
        Long id
) {}