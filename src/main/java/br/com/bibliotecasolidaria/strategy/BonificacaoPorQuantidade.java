package br.com.bibliotecasolidaria.strategy;

import br.com.bibliotecasolidaria.model.*;
import java.time.LocalDate;
import java.util.Optional;

public class BonificacaoPorQuantidade implements BonificacaoStrategy {
    private int quantidadeMinima;

    public BonificacaoPorQuantidade(int quantidadeMinima) {
        this.quantidadeMinima = quantidadeMinima;
    }

    @Override
    public Optional<Bonificacao> avaliar(Doador doador, Doacao doacao) {
        if (doacao instanceof DoacaoLivroFisico fisico) {
            if (fisico.getQuantidade() >= quantidadeMinima) {
                return Optional.of(new Bonificacao(null, doador.getId(), TipoBonificacao.SELO,
                        "Selo de Grande Doador por doar " + fisico.getQuantidade() + " livros físicos.",
                        LocalDate.now()));
            }
        }
        return Optional.empty();
    }
}
