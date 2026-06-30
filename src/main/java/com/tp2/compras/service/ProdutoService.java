package com.tp2.compras.service;

import com.tp2.compras.dto.ProdutoCadastroDTO;
import com.tp2.compras.dto.ProdutoResponseDTO;
import com.tp2.compras.dto.ProdutoUpdateDTO;
import com.tp2.compras.model.Produto;
import com.tp2.compras.model.StatusProduto;
import com.tp2.compras.model.Usuario;
import com.tp2.compras.repository.ProdutoRepository;
import com.tp2.compras.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Cadastra um novo produto no sistema, validando a duplicidade do código de barras.
     *
     * <p>Rastreamento de requisitos:
     * <ul>
     * <li>EU004 - Eu como usuário/admin quero poder criar, editar e deletar produtos.</li>
     * <li>EU005 - Iniciar cadastro pelo código de barras.</li>
     * <li>EU006 - Receber aviso se o produto já existe.</li>
     * </ul>
     *
     * <p>Pré-condições (Assertivas de Entrada):
     * <ul>
     * <li>O objeto {@code dto} não pode ser nulo.</li>
     * <li>Se {@code codigoBarras} for fornecido, não pode existir no banco.</li>
     * </ul>
     *
     * <p>Pós-condições (Assertivas de Saída):
     * <ul>
     * <li>Retorna a entidade {@link Produto} persistida, com ID gerado e status PENDENTE.</li>
     * </ul>
     *
     * @param dto dados do produto a ser salvo.
     * @return Produto recém-criado.
     */
    @Transactional
    public Produto cadastrar(ProdutoCadastroDTO dto) {
        Assert.notNull(dto, "O objeto de cadastro de produto não pode ser nulo");

        // Regra de Negócio: Validação de duplicidade pelo código de barras (EU006)
        if (dto.codigoBarras() != null && !dto.codigoBarras().isBlank()) {
            if (produtoRepository.existsByCodigoBarras(dto.codigoBarras())) {
                throw new IllegalArgumentException("Produto com este código de barras já existe no sistema.");
            }
        }

        // Busca o usuário criador se o ID for enviado
        Usuario criador = null;
        if (dto.criadoPorId() != null) {
            criador = usuarioRepository.findById(dto.criadoPorId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuário criador não encontrado."));
        }

        Produto novoProduto = Produto.builder()
                .nome(dto.nome())
                .codigoBarras(dto.codigoBarras())
                .descricao(dto.descricao())
                .categoria(dto.categoria())
                .status(StatusProduto.PENDENTE) // Produto criado por usuário entra como PENDENTE
                .criadoPor(criador)
                .build();

        return produtoRepository.save(novoProduto);
    }

    public ProdutoResponseDTO buscarPorId(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));
        return ProdutoResponseDTO.daEntidade(produto);
    }

    public List<ProdutoResponseDTO> listarTodos() {
        return produtoRepository.findAll().stream()
                .map(ProdutoResponseDTO::daEntidade)
                .collect(Collectors.toList());
    }

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

    @Transactional
    public void deletar(Long id) {
        if (!produtoRepository.existsById(id)) {
            throw new IllegalArgumentException("Produto não encontrado para deleção.");
        }
        produtoRepository.deleteById(id);
    }
}