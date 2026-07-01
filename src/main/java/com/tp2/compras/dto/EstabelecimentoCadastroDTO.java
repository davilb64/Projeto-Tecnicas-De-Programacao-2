package com.tp2.compras.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EstabelecimentoCadastroDTO(
        // Assertiva: Nome não nulo, não vazio, máx 150 caracteres.
        @NotBlank(message = "O nome do estabelecimento é obrigatório")
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres")
        String nome,

        // Assertiva: Endereço opcional, máx 255 caracteres.
        @Size(max = 255, message = "O endereço deve ter no máximo 255 caracteres")
        String endereco
) {}
