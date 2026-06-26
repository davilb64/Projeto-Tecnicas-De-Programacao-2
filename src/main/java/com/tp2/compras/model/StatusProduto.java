package com.tp2.compras.model;

/**
 * Status de aprovação de um produto no sistema.
 *
 * <p>Rastreamento de requisitos:
 * <ul>
 *   <li>EU004 - Usuário envia produto; admin aprova ou rejeita.</li>
 * </ul>
 */
public enum StatusProduto {
    PENDENTE,
    APROVADO,
    REJEITADO
}
