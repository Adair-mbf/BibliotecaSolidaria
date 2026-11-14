package br.com.bibliotecasolidaria.strategy;

import br.com.bibliotecasolidaria.model.Bonificacao;
import br.com.bibliotecasolidaria.model.Doacao;
import br.com.bibliotecasolidaria.model.DoacaoDinheiro;
import br.com.bibliotecasolidaria.model.Doador;
import br.com.bibliotecasolidaria.model.enums.TipoBonificacao;

import java.math.BigDecimal;
import java.util.Optional;

/** Selo de apoiador para doações em dinheiro a partir de um valor mínimo. */
public class BonificacaoPorValor implements BonificacaoStrategy {
    private final BigDecimal valorMinimo;

    public BonificacaoPorValor(BigDecimal valorMinimo) {
        this.valorMinimo = valorMinimo;
    }

    @Override
    public Optional<Bonificacao> avaliar(Doador doador, Doacao doacao) {
        if (doacao instanceof DoacaoDinheiro din && din.getValor() != null
                && din.getValor().compareTo(valorMinimo) >= 0) {
            return Optional.of(new Bonificacao(null, doador.getId(), TipoBonificacao.SELO,
                    "Selo de apoiador: doação de R$ " + din.getValor(), doacao.getDataRegistro()));
        }
        return Optional.empty();
    }
}
