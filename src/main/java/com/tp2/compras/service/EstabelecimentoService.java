package com.tp2.compras.service;

import com.tp2.compras.dto.EstabelecimentoCadastroDTO;
import com.tp2.compras.dto.EstabelecimentoResponseDTO;
import com.tp2.compras.dto.EstabelecimentoUpdateDTO;
import com.tp2.compras.model.Estabelecimento;
import com.tp2.compras.repository.EstabelecimentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EstabelecimentoService {

    private final EstabelecimentoRepository estabelecimentoRepository;

    /**
     * Cadastra um novo estabelecimento comercial de forma direta (Ação de Administrador).
     *
     * <p>Rastreamento de requisitos:
     * <ul>
     * <li>EU003 - Eu como admin quero poder criar estabelecimentos diretamente.</li>
     * </ul>
     *
     * <p>Pré-condições (Assertivas de Entrada):
     * <ul>
     * <li>O objeto {@code dto} não pode ser nulo.</li>
     * <li>Não deve existir outro estabelecimento com o mesmo nome exato.</li>
     * </ul>
     *
     * <p>Pós-condições (Assertivas de Saída):
     * <ul>
     * <li>Retorna a entidade devidamente persistida, com ID alocado e aprovado = true.</li>
     * </ul>
     */
    @Transactional
    public Estabelecimento cadastrar(EstabelecimentoCadastroDTO dto) {
        Assert.notNull(dto, "O DTO de cadastro não pode ser nulo");

        // Regra de Negócio: Evitar mercados duplicados com o mesmo nome exato
        if (estabelecimentoRepository.existsByNomeIgnoreCase(dto.nome())) {
            throw new IllegalArgumentException("Já existe um estabelecimento cadastrado ou sugerido com este nome.");
        }

        Estabelecimento novo = Estabelecimento.builder()
                .nome(dto.nome())
                .endereco(dto.endereco())
                .latitude(dto.latitude())
                .longitude(dto.longitude())
                .aprovado(true)
                .build();

        return estabelecimentoRepository.save(novo);
    }

    /**
     * Recebe uma sugestão de estabelecimento feita por um usuário comum.
     *
     * <p>Pós-condições (Assertivas de Saída):
     * <ul>
     * <li>Retorna o DTO salvo no banco de dados com a flag aprovado = false.</li>
     * </ul>
     */
    @Transactional
    public EstabelecimentoResponseDTO solicitar(EstabelecimentoCadastroDTO dto) {
        Assert.notNull(dto, "O DTO de solicitação não pode ser nulo");

        if (estabelecimentoRepository.existsByNomeIgnoreCase(dto.nome())) {
            throw new IllegalArgumentException("Este mercado já existe no sistema ou está na fila de aprovação.");
        }

        Estabelecimento novo = Estabelecimento.builder()
                .nome(dto.nome())
                .endereco(dto.endereco())
                .latitude(dto.latitude())
                .longitude(dto.longitude())
                .aprovado(false)
                .build();

        return EstabelecimentoResponseDTO.daEntidade(estabelecimentoRepository.save(novo));
    }

    /**
     * Busca os estabelecimentos que estão na fila de auditoria.
     */
    public List<EstabelecimentoResponseDTO> buscarPendentes() {
        return estabelecimentoRepository.findByAprovadoFalse().stream()
                .map(EstabelecimentoResponseDTO::daEntidade)
                .collect(Collectors.toList());
    }

    /**
     * Aprova um estabelecimento pendente para o catálogo oficial.
     */
    @Transactional
    public void aprovar(Long id) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Solicitação não encontrada."));

        estabelecimento.setAprovado(true);
        estabelecimentoRepository.save(estabelecimento);
    }

    public EstabelecimentoResponseDTO buscarPorId(Long id) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Estabelecimento não encontrado."));
        return EstabelecimentoResponseDTO.daEntidade(estabelecimento);
    }

    /**
     * Lista estabelecimentos do CATÁLOGO OFICIAL (aprovado = true), permitindo busca parcial (EU007).
     *
     * <p><b>Argumentação da corretude:</b>
     * Se o nome for fornecido, delega para a consulta parcial validada.
     * Caso contrário, retorna todos os aprovados. Filtra para nunca exibir sugestões pendentes.
     */
    public List<EstabelecimentoResponseDTO> listar(String nomeBusca) {
        List<Estabelecimento> resultados;
        if (nomeBusca != null && !nomeBusca.isBlank()) {
            resultados = estabelecimentoRepository.findByNomeContainingIgnoreCaseAndAprovadoTrue(nomeBusca);
        } else {
            resultados = estabelecimentoRepository.findByAprovadoTrue();
        }

        return resultados.stream()
                .map(EstabelecimentoResponseDTO::daEntidade)
                .collect(Collectors.toList());
    }

    @Transactional
    public EstabelecimentoResponseDTO atualizar(Long id, EstabelecimentoUpdateDTO dto) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Estabelecimento não encontrado."));

        if (dto.nome() != null && !dto.nome().isBlank()) {
            if (!estabelecimento.getNome().equalsIgnoreCase(dto.nome()) &&
                    estabelecimentoRepository.existsByNomeIgnoreCase(dto.nome())) {
                throw new IllegalArgumentException("Já existe outro estabelecimento com este nome.");
            }
            estabelecimento.setNome(dto.nome());
        }

        if (dto.endereco() != null) {
            estabelecimento.setEndereco(dto.endereco());
        }

        if (dto.latitude() != null) {
            estabelecimento.setLatitude(dto.latitude());
        }
        if (dto.longitude() != null) {
            estabelecimento.setLongitude(dto.longitude());
        }

        return EstabelecimentoResponseDTO.daEntidade(estabelecimentoRepository.save(estabelecimento));
    }

    @Transactional
    public void deletar(Long id) {
        if (!estabelecimentoRepository.existsById(id)) {
            throw new IllegalArgumentException("Estabelecimento não encontrado para deleção.");
        }
        estabelecimentoRepository.deleteById(id);
    }
}