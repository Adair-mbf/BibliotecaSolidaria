package br.com.bibliotecasolidaria.strategy;

import br.com.bibliotecasolidaria.model.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public class BonificacaoPorValor implements BonificacaoStrategy {
    private BigDecimal valorMinimo;

    public BonificacaoPorValor(BigDecimal valorMinimo) {
        this.valorMinimo = valorMinimo;
    }

    @Override
    public Optional<Bonificacao> avaliar(Doador doador, Doacao doacao) {
        if (doacao instanceof DoacaoDinheiro dinheiro) {
            if (dinheiro.getValor() != null && dinheiro.getValor().compareTo(valorMinimo) >= 0) {
                return Optional.of(new Bonificacao(null, doador.getId(), TipoBonificacao.CERTIFICADO,
                        "Certificado de Apoiador Ouro por doação financeira de R$ " + dinheiro.getValor(),
                        LocalDate.now()));
            }
        }
        return Optional.empty();
    }
}
