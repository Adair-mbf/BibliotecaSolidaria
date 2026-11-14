package br.com.bibliotecasolidaria.strategy;

import br.com.bibliotecasolidaria.model.Bonificacao;
import br.com.bibliotecasolidaria.model.Doacao;
import br.com.bibliotecasolidaria.model.DoacaoLivroFisico;
import br.com.bibliotecasolidaria.model.Doador;
import br.com.bibliotecasolidaria.model.enums.TipoBonificacao;

import java.util.Optional;

/** Certificado para quem doa ao menos N livros físicos de uma só vez. */
public class BonificacaoPorQuantidade implements BonificacaoStrategy {
    private final int quantidadeMinima;

    public BonificacaoPorQuantidade(int quantidadeMinima) {
        this.quantidadeMinima = quantidadeMinima;
    }

    @Override
    public Optional<Bonificacao> avaliar(Doador doador, Doacao doacao) {
        if (doacao instanceof DoacaoLivroFisico livros && livros.getQuantidade() >= quantidadeMinima) {
            return Optional.of(new Bonificacao(null, doador.getId(), TipoBonificacao.CERTIFICADO,
                    "Certificado por doar " + livros.getQuantidade() + " livros", doacao.getDataRegistro()));
        }
        return Optional.empty();
    }
}
