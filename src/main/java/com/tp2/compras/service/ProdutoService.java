package com.tp2.compras.service;

import com.tp2.compras.dto.ProdutoCadastroDTO;
import com.tp2.compras.dto.ProdutoResponseDTO;
import com.tp2.compras.dto.ProdutoUpdateDTO;
import com.tp2.compras.model.Produto;
import com.tp2.compras.model.StatusProduto;
import com.tp2.compras.model.Usuario;
import com.tp2.compras.model.VariacaoProduto;
import com.tp2.compras.repository.ProdutoRepository;
import com.tp2.compras.repository.UsuarioRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * Métodos de Produtos.
 */
@Service
@RequiredArgsConstructor
public class ProdutoService {

  private final ProdutoRepository produtoRepository;
  private final UsuarioRepository usuarioRepository;
  private final UsuarioService usuarioService;

  /**
   * Cadastra um novo produto no sistema, validando a duplicidade do código de barras
   * e já criando a variação inicial (peso/unidade) enviada pelo Front-end.
   */
  @Transactional
    public Produto cadastrar(ProdutoCadastroDTO dto) {
    Assert.notNull(dto, "O objeto de cadastro de produto não pode ser nulo");

    // Regra de Negócio: Validação de duplicidade pelo código de barras (EU006)
    if (dto.codigoBarras() != null && !dto.codigoBarras().isBlank()) {
      if (produtoRepository.existsByCodigoBarras(dto.codigoBarras())) {
        throw new IllegalArgumentException("Produto "
                        + "com este código de barras já existe no sistema.");
      }
    }

    // Busca o usuário criador se o ID for enviado
    Usuario criador = null;
    if (dto.criadoPorId() != null) {
      criador = usuarioRepository.findById(dto.criadoPorId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuário "
                            + "criador não encontrado."));
    }

    // Instancia o Produto (Pai)
    Produto novoProduto = Produto.builder()
                .nome(dto.nome())
                .codigoBarras(dto.codigoBarras())
                .descricao(dto.descricao())
                .categoria(dto.categoria())
                .status(StatusProduto.PENDENTE) // Produto criado por usuário entra como PENDENTE
                .criadoPor(criador)
                .variacoes(new ArrayList<>()) // Garante que a lista não é nula
                .build();

    // Cria a Variação (Filha) com os dados novos da API do OpenFoodFacts
    if (dto.variacaoDescricao() != null || dto.variacaoPeso() != null) {
      VariacaoProduto variacaoInicial = VariacaoProduto.builder()
                    .produto(novoProduto) // Amarra a filha ao pai
                    .descricao(dto.variacaoDescricao() != null
                            && !dto.variacaoDescricao().isBlank()
                            ? dto.variacaoDescricao() : "Embalagem Padrão")
                    .peso(dto.variacaoPeso() != null
                            ? BigDecimal.valueOf(dto.variacaoPeso()) : null)
                    .unidade(dto.variacaoUnidade())
                    .codigoBarras(dto.codigoBarras()) // A variação herda o código de barras lido
                    .build();

      // Adiciona a variação na lista do produto
      novoProduto.getVariacoes().add(variacaoInicial);
    }

    // O CascadeType.ALL na entidade Produto vai salvar os dois no banco de uma vez só
    return produtoRepository.save(novoProduto);
  }

  /**
   * Busca produto por ID.
   */
  public ProdutoResponseDTO buscarPorId(Long id) {
    Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));
    return ProdutoResponseDTO.daEntidade(produto);
  }

  /**
   * Lista todos os produtos.
   */
  public List<ProdutoResponseDTO> listarTodos() {
    return produtoRepository.findAll().stream()
                .map(ProdutoResponseDTO::daEntidade)
                .collect(Collectors.toList());
  }

  /**
   * Atualiza produto por ID.
   */
  @Transactional
    public ProdutoResponseDTO atualizar(Long id, ProdutoUpdateDTO dto) {
    Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));

    if (dto.nome() != null && !dto.nome().isBlank()) {
      produto.setNome(dto.nome());
    }
    if (dto.descricao() != null) {
      produto.setDescricao(dto.descricao());
    }
    if (dto.categoria() != null) {
      produto.setCategoria(dto.categoria());
    }

    return ProdutoResponseDTO.daEntidade(produtoRepository.save(produto));
  }

  /**
   * Deleta produto por ID.
   */
  @Transactional
    public void deletar(Long id) {
    if (!produtoRepository.existsById(id)) {
      throw new IllegalArgumentException("Produto não encontrado para deleção.");
    }
    produtoRepository.deleteById(id);
  }

  /**
   * Altera status de produto por ID.
   */
  @Transactional
    public void alterarStatus(Long id, String statusString) {
    Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));

    try {
      StatusProduto novoStatus = StatusProduto.valueOf(statusString.toUpperCase());
      produto.setStatus(novoStatus);

      if (novoStatus == StatusProduto.APROVADO && produto.getCriadoPor() != null) {
        usuarioService.adicionarXp(produto.getCriadoPor().getId(), 50);
      }

      produtoRepository.save(produto);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Status de produto inválido.");
    }
  }
}