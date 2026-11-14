package br.com.bibliotecasolidaria.strategy;

import br.com.bibliotecasolidaria.model.Emprestimo;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Valor fixo por dia de atraso. */
public class MultaFixaDiaria implements CalculoMultaStrategy {
    private final BigDecimal valorDia;

    public MultaFixaDiaria(BigDecimal valorDia) {
        if (valorDia == null || valorDia.signum() < 0) {
            throw new IllegalArgumentException("O valor diário da multa não pode ser negativo.");
        }
        this.valorDia = valorDia;
    }

    @Override
    public BigDecimal calcular(int diasAtraso, Emprestimo emprestimo) {
        if (diasAtraso <= 0) {
            return BigDecimal.ZERO.setScale(2);
        }
        return valorDia.multiply(BigDecimal.valueOf(diasAtraso)).setScale(2, RoundingMode.HALF_UP);
    }
}
