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
     * Cadastra um novo estabelecimento comercial.
     *
     * <p>Rastreamento de requisitos:
     * <ul>
     * <li>EU003 - Eu como usuário/admin quero poder criar estabelecimentos.</li>
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
     * <li>Retorna a entidade devidamente persistida e com ID alocado.</li>
     * </ul>
     */
    @Transactional
    public Estabelecimento cadastrar(EstabelecimentoCadastroDTO dto) {
        Assert.notNull(dto, "O DTO de cadastro não pode ser nulo");

        // Regra de Negócio: Evitar mercados duplicados com o mesmo nome exato
        if (estabelecimentoRepository.existsByNomeIgnoreCase(dto.nome())) {
            throw new IllegalArgumentException("Já existe um estabelecimento cadastrado com este nome.");
        }

        Estabelecimento novo = Estabelecimento.builder()
                .nome(dto.nome())
                .endereco(dto.endereco())
                .build();

        return estabelecimentoRepository.save(novo);
    }

    public EstabelecimentoResponseDTO buscarPorId(Long id) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Estabelecimento não encontrado."));
        return EstabelecimentoResponseDTO.daEntidade(estabelecimento);
    }

    /**
     * Lista estabelecimentos, permitindo busca opcional por trecho do nome (EU007).
     *
     * <p><b>Argumentação da corretude:</b>
     * Se o nome for fornecido, delega para a consulta parcial ignorando caixa (findByNomeContainingIgnoreCase).
     * Caso contrário, retorna todos (findAll). O resultado é sempre mapeado de forma segura para DTOs.
     */
    public List<EstabelecimentoResponseDTO> listar(String nomeBusca) {
        List<Estabelecimento> resultados;
        if (nomeBusca != null && !nomeBusca.isBlank()) {
            resultados = estabelecimentoRepository.findByNomeContainingIgnoreCase(nomeBusca);
        } else {
            resultados = estabelecimentoRepository.findAll();
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
            // Verifica se o novo nome já não pertence a outro mercado
            if (!estabelecimento.getNome().equalsIgnoreCase(dto.nome()) &&
                    estabelecimentoRepository.existsByNomeIgnoreCase(dto.nome())) {
                throw new IllegalArgumentException("Já existe outro estabelecimento com este nome.");
            }
            estabelecimento.setNome(dto.nome());
        }

        if (dto.endereco() != null) {
            estabelecimento.setEndereco(dto.endereco());
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