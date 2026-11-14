package br.com.bibliotecasolidaria.strategy;

import br.com.bibliotecasolidaria.model.Emprestimo;
import br.com.bibliotecasolidaria.model.enums.StatusEmprestimo;

import java.time.Clock;
import java.time.LocalDate;

/** Permite até {@code maxRenovacoes} renovações de empréstimos ativos e não vencidos. */
public class LimiteFixoRenovacao implements PoliticaRenovacao {
    private final int maxRenovacoes;
    private final int diasPorRenovacao;
    private final Clock clock;

    public LimiteFixoRenovacao(int maxRenovacoes, int diasPorRenovacao, Clock clock) {
        if (maxRenovacoes < 0 || diasPorRenovacao < 1) {
            throw new IllegalArgumentException("Limite de renovações e dias por renovação inválidos.");
        }
        this.maxRenovacoes = maxRenovacoes;
        this.diasPorRenovacao = diasPorRenovacao;
        this.clock = clock;
    }

    public LimiteFixoRenovacao(int maxRenovacoes, int diasPorRenovacao) {
        this(maxRenovacoes, diasPorRenovacao, Clock.systemDefaultZone());
    }

    @Override
    public boolean podeRenovar(Emprestimo e) {
        return e.getStatus() == StatusEmprestimo.ATIVO
                && !e.estaAtrasado(LocalDate.now(clock))
                && e.getRenovacoes() < maxRenovacoes;
    }

    @Override
    public LocalDate calcularNovoPrazo(Emprestimo e) {
        return e.getDataPrevistaDevolucao().plusDays(diasPorRenovacao);
    }
}
