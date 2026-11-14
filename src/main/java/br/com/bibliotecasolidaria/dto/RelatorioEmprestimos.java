package br.com.bibliotecasolidaria.dto;

import br.com.bibliotecasolidaria.model.Emprestimo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record RelatorioEmprestimos(LocalDate inicio, LocalDate fim, List<Emprestimo> emprestimos, int ativos,
                                   int atrasados, int devolvidos, int multasGeradas, BigDecimal valorMultas) {
    public int total() {
        return emprestimos.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Relatório de empréstimos de ").append(inicio).append(" a ").append(fim).append('\n')
                .append("  Total: ").append(total()).append(" | ativos: ").append(ativos)
                .append(" | atrasados: ").append(atrasados).append(" | devolvidos: ").append(devolvidos).append('\n')
                .append("  Multas geradas no período: ").append(multasGeradas)
                .append(" (R$ ").append(valorMultas).append(")");
        emprestimos.forEach(e -> sb.append("\n  - ").append(e));
        return sb.toString();
    }
}
