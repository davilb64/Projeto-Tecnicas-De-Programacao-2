package com.tp2.compras.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO)
 * Utilizado para receber os dados necessários para o cadastro de um produto.
 */
public record ProdutoCadastroDTO(

        // Nome obrigatório e com tamanho máximo
        @NotBlank(message = "O nome do produto é obrigatório")
        @Size(max = 100, message = "O nome deve possuir no máximo 100 caracteres")
        String nome,

        // Descrição obrigatória
        @NotBlank(message = "A descrição é obrigatória")
        @Size(max = 500, message = "A descrição deve possuir no máximo 500 caracteres")
        String descricao,

        // Preço obrigatório e maior que zero
        @NotNull(message = "O preço é obrigatório")
        @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero")
        Double preco,

        // Quantidade obrigatória e positiva
        @NotNull(message = "A quantidade é obrigatória")
        @Positive(message = "A quantidade deve ser maior que zero")
        Integer quantidade

) {}