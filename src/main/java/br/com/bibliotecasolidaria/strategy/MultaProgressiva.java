package br.com.bibliotecasolidaria.strategy;

import br.com.bibliotecasolidaria.model.Emprestimo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Valor diário crescente por faixas de atraso, com teto opcional. Dias além da última faixa
 * usam o valor diário da última faixa.
 */
public class MultaProgressiva implements CalculoMultaStrategy {
    private final List<FaixaMulta> faixas;
    private final BigDecimal teto;

    public MultaProgressiva(List<FaixaMulta> faixas, BigDecimal teto) {
        if (faixas == null || faixas.isEmpty()) {
            throw new IllegalArgumentException("Informe ao menos uma faixa de multa.");
        }
        List<FaixaMulta> ordenadas = new ArrayList<>(faixas);
        ordenadas.sort(Comparator.comparingInt(FaixaMulta::diaInicial));
        for (int i = 1; i < ordenadas.size(); i++) {
            if (ordenadas.get(i).diaInicial() <= ordenadas.get(i - 1).diaFinal()) {
                throw new IllegalArgumentException("As faixas de multa não podem se sobrepor.");
            }
        }
        if (teto != null && teto.signum() < 0) {
            throw new IllegalArgumentException("O teto da multa não pode ser negativo.");
        }
        this.faixas = List.copyOf(ordenadas);
        this.teto = teto;
    }

    @Override
    public BigDecimal calcular(int diasAtraso, Emprestimo emprestimo) {
        BigDecimal total = BigDecimal.ZERO;
        for (int dia = 1; dia <= diasAtraso; dia++) {
            total = total.add(valorDoDia(dia));
        }
        if (teto != null && total.compareTo(teto) > 0) {
            total = teto;
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal valorDoDia(int dia) {
        for (FaixaMulta f : faixas) {
            if (f.contem(dia)) {
                return f.valorDia();
            }
        }
        FaixaMulta ultima = faixas.get(faixas.size() - 1);
        return dia > ultima.diaFinal() ? ultima.valorDia() : BigDecimal.ZERO;
    }
}
