package com.tp2.compras.service;

import com.tp2.compras.dto.VariacaoProdutoCadastroDTO;
import com.tp2.compras.dto.VariacaoProdutoResponseDTO;
import com.tp2.compras.dto.VariacaoProdutoUpdateDTO;
import com.tp2.compras.model.Produto;
import com.tp2.compras.model.VariacaoProduto;
import com.tp2.compras.repository.ProdutoRepository;
import com.tp2.compras.repository.VariacaoProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VariacaoProdutoService {

    private final VariacaoProdutoRepository variacaoRepository;
    private final ProdutoRepository produtoRepository;

    /**
     * Cadastra uma nova variação vinculada a um produto existente.
     *
     * <p>Rastreamento de requisitos:
     * <ul>
     * <li>EU005 - Cadastro de variação pelo código de barras.</li>
     * <li>EU006 - Agrupar variações e detectar duplicatas.</li>
     * </ul>
     *
     * <p>Pré-condições (Assertivas de Entrada):
     * <ul>
     * <li>O DTO não pode ser nulo.</li>
     * <li>O produto pai deve existir no banco de dados.</li>
     * <li>O código de barras (se enviado) deve ser único (verificado via repository)[cite: 16].</li>
     * </ul>
     *
     * <p>Pós-condições (Assertivas de Saída):
     * <ul>
     * <li>Retorna a variação salva e vinculada ao produto pai.</li>
     * </ul>
     */
    @Transactional
    public VariacaoProduto cadastrar(VariacaoProdutoCadastroDTO dto) {
        Assert.notNull(dto, "O DTO de cadastro de variação não pode ser nulo");

        Produto produto = produtoRepository.findById(dto.produtoId())
                .orElseThrow(() -> new IllegalArgumentException("Produto pai não encontrado."));

        if (dto.codigoBarras() != null && !dto.codigoBarras().isBlank()) {
            if (variacaoRepository.existsByCodigoBarras(dto.codigoBarras())) {
                throw new IllegalArgumentException("Variação com este código de barras já existe.");
            }
        }

        VariacaoProduto novaVariacao = VariacaoProduto.builder()
                .produto(produto)
                .descricao(dto.descricao())
                .peso(dto.peso())
                .unidade(dto.unidade())
                .codigoBarras(dto.codigoBarras())
                .build();

        return variacaoRepository.save(novaVariacao);
    }

    public List<VariacaoProdutoResponseDTO> listarPorProduto(Long produtoId) {
        return variacaoRepository.findByProdutoId(produtoId).stream()
                .map(VariacaoProdutoResponseDTO::daEntidade)
                .collect(Collectors.toList());
    }

    @Transactional
    public VariacaoProdutoResponseDTO atualizar(Long id, VariacaoProdutoUpdateDTO dto) {
        VariacaoProduto variacao = variacaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Variação não encontrada."));

        if (dto.descricao() != null && !dto.descricao().isBlank()) variacao.setDescricao(dto.descricao());
        if (dto.peso() != null) variacao.setPeso(dto.peso());
        if (dto.unidade() != null) variacao.setUnidade(dto.unidade());

        return VariacaoProdutoResponseDTO.daEntidade(variacaoRepository.save(variacao));
    }

    @Transactional
    public void deletar(Long id) {
        if (!variacaoRepository.existsById(id)) {
            throw new IllegalArgumentException("Variação não encontrada.");
        }
        variacaoRepository.deleteById(id);
    }
}