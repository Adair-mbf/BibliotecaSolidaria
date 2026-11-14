package br.com.bibliotecasolidaria.dto;

import br.com.bibliotecasolidaria.model.Doacao;
import br.com.bibliotecasolidaria.model.enums.StatusDoacao;
import br.com.bibliotecasolidaria.model.enums.TipoDoacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/** Totais de doações do período; livros e valores consideram apenas doações aceitas. */
public record RelatorioDoacoes(LocalDate inicio, LocalDate fim, List<Doacao> doacoes,
                               Map<TipoDoacao, Integer> porTipo, Map<StatusDoacao, Integer> porStatus,
                               int livrosFisicosRecebidos, BigDecimal totalArrecadado) {
    public int total() {
        return doacoes.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Relatório de doações de ").append(inicio).append(" a ").append(fim).append('\n')
                .append("  Total: ").append(total()).append(" | por tipo: ").append(porTipo)
                .append(" | por status: ").append(porStatus).append('\n')
                .append("  Livros físicos recebidos: ").append(livrosFisicosRecebidos)
                .append(" | arrecadado: R$ ").append(totalArrecadado);
        doacoes.forEach(d -> sb.append("\n  - ").append(d));
        return sb.toString();
    }
}
