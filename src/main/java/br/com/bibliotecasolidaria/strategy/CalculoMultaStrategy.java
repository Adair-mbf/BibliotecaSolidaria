package br.com.bibliotecasolidaria.strategy;

import br.com.bibliotecasolidaria.model.Emprestimo;

import java.math.BigDecimal;

/** Strategy: regra de cálculo do valor da multa por atraso. */
public interface CalculoMultaStrategy {
    BigDecimal calcular(int diasAtraso, Emprestimo emprestimo);
}
