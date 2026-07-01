package com.tp2.compras.service;

import com.tp2.compras.dto.ComparacaoMercadoDTO;
import com.tp2.compras.model.Estabelecimento;
import com.tp2.compras.model.ItemLista;
import com.tp2.compras.model.ListaCompras;
import com.tp2.compras.model.Preco;
import com.tp2.compras.repository.EstabelecimentoRepository;
import com.tp2.compras.repository.ListaComprasRepository;
import com.tp2.compras.repository.PrecoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComparacaoService {

    private final ListaComprasRepository listaRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;
    private final PrecoRepository precoRepository;

    public List<ComparacaoMercadoDTO> compararListaNosMercados(Long listaId) {
        ListaCompras lista = listaRepository.findById(listaId)
                .orElseThrow(() -> new IllegalArgumentException("Lista não encontrada."));

        List<Estabelecimento> mercados = estabelecimentoRepository.findAll();
        List<ComparacaoMercadoDTO> comparacoes = new ArrayList<>();

        // Se a lista estiver vazia, retorna zerado para não dar erro
        if (lista.getItens().isEmpty()) {
            for (Estabelecimento mercado : mercados) {
                comparacoes.add(new ComparacaoMercadoDTO(
                        mercado.getId(),
                        mercado.getNome(),
                        BigDecimal.ZERO,
                        0,
                        0,
                        "Sua lista está vazia.",
                        BigDecimal.ZERO,
                        BigDecimal.ZERO
                ));
            }
            return comparacoes;
        }

        // Pega os IDs das variações que estão na lista do usuário
        List<Long> variacaoIds = lista.getItens().stream()
                .map(item -> item.getVariacao().getId())
                .collect(Collectors.toList());

        int totalItensLista = lista.getItens().size();

        // Mapa rápido para sabermos a quantidade que o usuário quer de cada item (Ex: 3x Coca-Cola)
        Map<Long, Integer> mapaQuantidades = lista.getItens().stream()
                .collect(Collectors.toMap(
                        item -> item.getVariacao().getId(),
                        ItemLista::getQuantidade
                ));

        // Avalia a lista mercado por mercado
        for (Estabelecimento mercado : mercados) {
            // Usa a query customizada que traz apenas o preço mais recente de cada item da lista neste mercado
            List<Preco> precosAtuais = precoRepository.buscarPrecosAtuaisDaListaNoMercado(variacaoIds, mercado.getId());

            BigDecimal valorTotal = BigDecimal.ZERO;
            int itensEncontrados = precosAtuais.size();

            // Soma o (Valor do Item * Quantidade Desejada)
            for (Preco preco : precosAtuais) {
                int quantidade = mapaQuantidades.getOrDefault(preco.getVariacao().getId(), 1);
                BigDecimal subtotal = preco.getValor().multiply(BigDecimal.valueOf(quantidade));
                valorTotal = valorTotal.add(subtotal);
            }

            // Gera a mensagem amigável para o Front-end
            String mensagem;
            if (itensEncontrados == 0) {
                mensagem = "Nenhum preço registrado.";
            } else if (itensEncontrados < totalItensLista) {
                int faltam = totalItensLista - itensEncontrados;
                mensagem = "Faltam " + faltam + " itens.";
            } else {
                mensagem = "Lista completa!";
            }

            comparacoes.add(new ComparacaoMercadoDTO(
                    mercado.getId(),
                    mercado.getNome(),
                    valorTotal,
                    itensEncontrados,
                    totalItensLista,
                    mensagem,
                    mercado.getLatitude(),
                    mercado.getLongitude()
            ));
        }

        // Ordena do mais barato pro mais caro
        comparacoes.sort((c1, c2) -> {
            // Mercados sem nenhum item cadastrado vão pro final da fila
            if (c1.itensEncontrados() == 0 && c2.itensEncontrados() > 0) return 1;
            if (c2.itensEncontrados() == 0 && c1.itensEncontrados() > 0) return -1;

            return c1.valorTotal().compareTo(c2.valorTotal());
        });

        return comparacoes;
    }
}