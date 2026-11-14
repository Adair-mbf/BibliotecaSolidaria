package br.com.bibliotecasolidaria.strategy;

import br.com.bibliotecasolidaria.model.Emprestimo;

import java.time.LocalDate;

/** Strategy: regra de renovação de empréstimos. */
public interface PoliticaRenovacao {
    boolean podeRenovar(Emprestimo emprestimo);

    LocalDate calcularNovoPrazo(Emprestimo emprestimo);
}
