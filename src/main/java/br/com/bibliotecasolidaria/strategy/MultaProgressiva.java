package br.com.bibliotecasolidaria.strategy;

import br.com.bibliotecasolidaria.model.Emprestimo;
import java.math.BigDecimal;
import java.util.List;

public class MultaProgressiva implements CalculoMultaStrategy {
    private List<FaixaMulta> faixas;
    private BigDecimal teto;

    public MultaProgressiva(List<FaixaMulta> faixas, BigDecimal teto) {
        this.faixas = faixas;
        this.teto = teto;
    }

    @Override
    public BigDecimal calcular(int diasAtraso, Emprestimo emprestimo) {
        if (diasAtraso <= 0) return BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;
        for (int d = 1; d <= diasAtraso; d++) {
            BigDecimal taxaDoDia = BigDecimal.valueOf(2.50);
            if (faixas != null) {
                for (FaixaMulta f : faixas) {
                    if (d >= f.getDiaInicial() && d <= f.getDiaFinal()) {
                        taxaDoDia = f.getValorDia();
                        break;
                    }
                }
            }
            total = total.add(taxaDoDia);
        }
        if (teto != null && total.compareTo(teto) > 0) {
            total = teto;
        }
        return total;
    }
}
