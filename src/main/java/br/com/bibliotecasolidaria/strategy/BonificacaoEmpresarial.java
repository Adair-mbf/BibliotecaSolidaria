package br.com.bibliotecasolidaria.strategy;

import br.com.bibliotecasolidaria.model.Bonificacao;
import br.com.bibliotecasolidaria.model.Doacao;
import br.com.bibliotecasolidaria.model.DoacaoLivroFisico;
import br.com.bibliotecasolidaria.model.Doador;
import br.com.bibliotecasolidaria.model.enums.TipoBonificacao;

import java.util.Optional;

/** Parceria institucional para pessoas jurídicas que doam ao menos N livros físicos. */
public class BonificacaoEmpresarial implements BonificacaoStrategy {
    private final int quantidadeMinima;

    public BonificacaoEmpresarial(int quantidadeMinima) {
        this.quantidadeMinima = quantidadeMinima;
    }

    @Override
    public Optional<Bonificacao> avaliar(Doador doador, Doacao doacao) {
        if (doador.isPessoaJuridica() && doacao instanceof DoacaoLivroFisico livros
                && livros.getQuantidade() >= quantidadeMinima) {
            return Optional.of(new Bonificacao(null, doador.getId(), TipoBonificacao.PARCERIA,
                    "Parceria institucional: doação empresarial de " + livros.getQuantidade() + " livros",
                    doacao.getDataRegistro()));
        }
        return Optional.empty();
    }
}
