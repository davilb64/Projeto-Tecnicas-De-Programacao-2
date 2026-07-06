package com.tp2.compras.service;

import com.tp2.compras.dto.EstimativaPrecoDTO;
import com.tp2.compras.dto.PrecoCadastroDTO;
import com.tp2.compras.dto.PrecoResponseDTO;
import com.tp2.compras.dto.PrecoUpdateDTO;
import com.tp2.compras.model.Estabelecimento;
import com.tp2.compras.model.Preco;
import com.tp2.compras.model.Usuario;
import com.tp2.compras.model.VariacaoProduto;
import com.tp2.compras.repository.EstabelecimentoRepository;
import com.tp2.compras.repository.PrecoRepository;
import com.tp2.compras.repository.VariacaoProdutoRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * Métodos de Preço.
 */
@Service
@RequiredArgsConstructor
public class PrecoService {
  private final PrecoRepository precoRepository;
  private final VariacaoProdutoRepository variacaoRepository;
  private final EstabelecimentoRepository estabelecimentoRepository;
  private final UsuarioService usuarioService;

  /**
   * Registra preço.
   */
  @Transactional
    public PrecoResponseDTO registrarPreco(PrecoCadastroDTO dto) {
    Assert.notNull(dto, "O DTO de cadastro de preço não pode ser nulo");

    VariacaoProduto variacao = variacaoRepository.findById(dto.variacaoId())
                .orElseThrow(() -> new IllegalArgumentException("Variação de "
                        + "produto não encontrada."));

    Estabelecimento mercado = estabelecimentoRepository.findById(dto.estabelecimentoId())
                .orElseThrow(() -> new IllegalArgumentException("Estabelecimento não encontrado."));

    // Cria uma entidade "oca" só com o ID para salvar a FK
    Usuario usuarioRef = new Usuario();
    usuarioRef.setId(dto.usuarioId());

    Preco novoPreco = Preco.builder()
                .variacao(variacao)
                .estabelecimento(mercado)
                .registradoPor(usuarioRef)
                .valor(dto.valor())
                .build();

    Preco salvo = precoRepository.save(novoPreco);

    usuarioService.adicionarXp(dto.usuarioId(), 10);

    return PrecoResponseDTO.daEntidade(salvo);
  }

  /**
   * Busca preço por ID.
   */
  public PrecoResponseDTO buscarPorId(Long id) {
    Preco preco = precoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Registro "
                        + "de preço não encontrado."));
    return PrecoResponseDTO.daEntidade(preco);
  }

  /**
   * Lista todos os preços.
   */
  public List<PrecoResponseDTO> listarTodos() {
    return precoRepository.findAll().stream()
                .map(PrecoResponseDTO::daEntidade)
                .collect(Collectors.toList());
  }

  /**
   * Busca histórico específico pra renderizar gráficos depois.
   */
  public List<PrecoResponseDTO> buscarHistorico(Long variacaoId, Long estabelecimentoId) {
    return precoRepository
                .findByVariacaoIdAndEstabelecimentoIdOrderByDataRegistroDesc(variacaoId,
                        estabelecimentoId)
                .stream()
                .map(PrecoResponseDTO::daEntidade)
                .collect(Collectors.toList());
  }

  /**
   * Atualiza preço por id.
   */
  @Transactional
    public PrecoResponseDTO atualizar(Long id, PrecoUpdateDTO dto) {
    Assert.notNull(dto, "O DTO de atualização não pode ser nulo");

    Preco preco = precoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Registro "
                        + "de preço não encontrado."));

    preco.setValor(dto.valor());

    // A data de registro original é mantida. Se quiser atualizar a data da alteração,
    // seria ideal ter um campo `atualizadoEm` na entidade.

    return PrecoResponseDTO.daEntidade(precoRepository.save(preco));
  }

  /**
   * Deleta preço por ID.
   */
  @Transactional
    public void deletar(Long id) {
    if (!precoRepository.existsById(id)) {
      throw new IllegalArgumentException("Registro de preço não encontrado para deleção.");
    }
    precoRepository.deleteById(id);
  }

  /**
   * Estimativa de valor por ID da variação e estabelecimento.
   */
  public EstimativaPrecoDTO obterEstimativaItem(Long variacaoId, Long estabelecimentoId) {
    // Tenta achar o preço exato neste mercado
    var precoExato = precoRepository
                .findFirstByVariacaoIdAndEstabelecimentoIdOrderByDataRegistroDesc(variacaoId,
                        estabelecimentoId);

    if (precoExato.isPresent()) {
      return new EstimativaPrecoDTO(precoExato.get().getValor(), "EXATO_MERCADO");
    }

    // Se não achar, calcula a média geral da comunidade
    Double media = precoRepository.calcularMediaPorVariacao(variacaoId);
    if (media != null) {
      // Arredonda para 2 casas decimais
      BigDecimal valorMedio = BigDecimal.valueOf(media)
                    .setScale(2, java.math.RoundingMode.HALF_UP);
      return new EstimativaPrecoDTO(valorMedio, "MEDIA_GERAL");
    }

    // Se ninguém nunca cadastrou esse produto em lugar nenhum
    return new EstimativaPrecoDTO(BigDecimal.ZERO, "NENHUM");
  }
}