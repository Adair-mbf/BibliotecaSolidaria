package br.com.bibliotecasolidaria.strategy;

import br.com.bibliotecasolidaria.model.Emprestimo;
import java.math.BigDecimal;

public class MultaFixaDiaria implements CalculoMultaStrategy {
    private BigDecimal valorDia;

    public MultaFixaDiaria(BigDecimal valorDia) {
        this.valorDia = valorDia != null ? valorDia : new BigDecimal("2.00");
    }

    @Override
    public BigDecimal calcular(int diasAtraso, Emprestimo emprestimo) {
        if (diasAtraso <= 0) return BigDecimal.ZERO;
        return valorDia.multiply(BigDecimal.valueOf(diasAtraso));
    }
}
