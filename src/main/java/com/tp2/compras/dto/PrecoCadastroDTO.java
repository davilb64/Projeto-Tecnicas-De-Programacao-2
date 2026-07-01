package com.tp2.compras.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Data Transfer Object para recebimento de dados de um novo preço colaborativo.
 *
 * <p>Rastreamento de requisitos:
 * <ul>
 *   <li>EU009 - Eu como usuário quero poder informar o preço de um produto em um mercado.</li>
 * </ul>
 *
 * <p><b>Argumentação da corretude:</b>
 * Utiliza o padrão Record introduzido no Java 14 para garantir imutabilidade.
 * As validações do Bean Validation (@NotNull, @DecimalMin) garantem que requisições
 * malformadas sejam interceptadas no Controller com HTTP 400 antes de onerar a camada de serviço.
 */
public record PrecoCadastroDTO(

        @NotNull(message = "O ID da variação do produto é obrigatório")
        Long variacaoId,

        @NotNull(message = "O ID do estabelecimento (mercado) é obrigatório")
        Long estabelecimentoId,

        @NotNull(message = "O ID do usuário que está informando é obrigatório")
        Long usuarioId,

        @NotNull(message = "O valor monetário é obrigatório")
        @DecimalMin(value = "0.01", message = "O valor deve ser de pelo menos R$ 0,01")
        BigDecimal valor
) {}