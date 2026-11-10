package br.com.bibliotecasolidaria.strategy;

import br.com.bibliotecasolidaria.model.Emprestimo;
import java.math.BigDecimal;

public interface CalculoMultaStrategy {
    BigDecimal calcular(int diasAtraso, Emprestimo emprestimo);
}
