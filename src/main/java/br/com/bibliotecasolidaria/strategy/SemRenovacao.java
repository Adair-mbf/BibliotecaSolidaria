package br.com.bibliotecasolidaria.strategy;

import br.com.bibliotecasolidaria.model.Emprestimo;
import java.time.LocalDate;

public class SemRenovacao implements PoliticaRenovacao {
    @Override
    public boolean podeRenovar(Emprestimo emprestimo) {
        return false;
    }

    @Override
    public LocalDate calcularNovoPrazo(Emprestimo emprestimo) {
        return emprestimo != null ? emprestimo.getDataPrevistaDevolucao() : LocalDate.now();
    }
}
