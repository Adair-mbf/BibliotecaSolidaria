package br.com.bibliotecasolidaria.strategy;

import java.math.BigDecimal;

public class FaixaMulta {
    private int diaInicial;
    private int diaFinal;
    private BigDecimal valorDia;

    public FaixaMulta(int diaInicial, int diaFinal, BigDecimal valorDia) {
        this.diaInicial = diaInicial;
        this.diaFinal = diaFinal;
        this.valorDia = valorDia;
    }

    public int getDiaInicial() { return diaInicial; }
    public int getDiaFinal() { return diaFinal; }
    public BigDecimal getValorDia() { return valorDia; }
}
