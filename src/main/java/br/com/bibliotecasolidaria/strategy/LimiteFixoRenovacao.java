package br.com.bibliotecasolidaria.strategy;

import br.com.bibliotecasolidaria.model.Emprestimo;
import java.time.LocalDate;

public class LimiteFixoRenovacao implements PoliticaRenovacao {
    private int maxRenovacoes;
    private int diasPorRenovacao;

    public LimiteFixoRenovacao(int maxRenovacoes, int diasPorRenovacao) {
        this.maxRenovacoes = maxRenovacoes;
        this.diasPorRenovacao = diasPorRenovacao;
    }

    @Override
    public boolean podeRenovar(Emprestimo emprestimo) {
        if (emprestimo == null) return false;
        if (emprestimo.estaAtrasado()) return false;
        return emprestimo.getRenovacoes() < maxRenovacoes;
    }

    @Override
    public LocalDate calcularNovoPrazo(Emprestimo emprestimo) {
        LocalDate base = (emprestimo.getDataPrevistaDevolucao() != null)
                ? emprestimo.getDataPrevistaDevolucao()
                : LocalDate.now();
        return base.plusDays(diasPorRenovacao);
    }
}
