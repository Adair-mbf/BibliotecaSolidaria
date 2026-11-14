package br.com.bibliotecasolidaria.strategy;

import br.com.bibliotecasolidaria.exception.NegocioException;
import br.com.bibliotecasolidaria.model.Emprestimo;

import java.time.LocalDate;

/** Política que não admite renovações. */
public class SemRenovacao implements PoliticaRenovacao {
    @Override
    public boolean podeRenovar(Emprestimo emprestimo) {
        return false;
    }

    @Override
    public LocalDate calcularNovoPrazo(Emprestimo emprestimo) {
        throw new NegocioException("Esta política não permite renovações.");
    }
}
