package br.com.bibliotecasolidaria.strategy;

import java.math.BigDecimal;

/** Faixa de dias de atraso (inclusive) com seu valor diário. */
public record FaixaMulta(int diaInicial, int diaFinal, BigDecimal valorDia) {
    public FaixaMulta {
        if (diaInicial < 1 || diaFinal < diaInicial) {
            throw new IllegalArgumentException("Faixa de multa inválida: " + diaInicial + " a " + diaFinal + ".");
        }
        if (valorDia == null || valorDia.signum() < 0) {
            throw new IllegalArgumentException("O valor diário da faixa não pode ser negativo.");
        }
    }

    public boolean contem(int dia) {
        return dia >= diaInicial && dia <= diaFinal;
    }
}
