package com.tp2.compras.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProdutoCadastroDTO(
        // Assertiva: Nome não pode ser vazio e deve ter no máximo 150 caracteres.
        @NotBlank(message = "O nome do produto é obrigatório")
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres")
        String nome,

        // Assertiva: máximo 50 caracteres (EU005).
        @Size(max = 50, message = "O código de barras deve ter no máximo 50 caracteres")
        String codigoBarras,

        String descricao,

        @Size(max = 100, message = "A categoria deve ter no máximo 100 caracteres")
        String categoria,

        // ID do usuário que está logado e enviando o produto
        Long criadoPorId
) {}