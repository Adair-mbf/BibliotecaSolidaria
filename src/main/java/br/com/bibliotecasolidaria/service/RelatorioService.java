package br.com.bibliotecasolidaria.service;

import br.com.bibliotecasolidaria.dto.RelatorioDoacoes;
import br.com.bibliotecasolidaria.dto.RelatorioEmprestimos;
import br.com.bibliotecasolidaria.exception.ValidacaoException;
import br.com.bibliotecasolidaria.model.Doacao;
import br.com.bibliotecasolidaria.model.DoacaoDinheiro;
import br.com.bibliotecasolidaria.model.DoacaoLivroFisico;
import br.com.bibliotecasolidaria.model.Emprestimo;
import br.com.bibliotecasolidaria.model.Multa;
import br.com.bibliotecasolidaria.model.enums.StatusDoacao;
import br.com.bibliotecasolidaria.model.enums.StatusEmprestimo;
import br.com.bibliotecasolidaria.model.enums.TipoDoacao;
import br.com.bibliotecasolidaria.persistence.DoacaoRepositorio;
import br.com.bibliotecasolidaria.persistence.EmprestimoRepositorio;
import br.com.bibliotecasolidaria.persistence.MultaRepositorio;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class RelatorioService {
    private final EmprestimoRepositorio emprestimos;
    private final MultaRepositorio multas;
    private final DoacaoRepositorio doacoes;
    private final Clock clock;

    public RelatorioService(EmprestimoRepositorio emprestimos, MultaRepositorio multas, DoacaoRepositorio doacoes,
                            Clock clock) {
        this.emprestimos = emprestimos;
        this.multas = multas;
        this.doacoes = doacoes;
        this.clock = clock;
    }

    public RelatorioEmprestimos emprestimosPorPeriodo(LocalDate inicio, LocalDate fim) {
        validarPeriodo(inicio, fim);
        LocalDate hoje = LocalDate.now(clock);
        List<Emprestimo> lista = emprestimos.listarPorPeriodo(inicio, fim);
        int ativos = 0, atrasados = 0, devolvidos = 0;
        for (Emprestimo e : lista) {
            if (e.getStatus() == StatusEmprestimo.DEVOLVIDO) {
                devolvidos++;
            } else {
                ativos++;
                if (e.estaAtrasado(hoje)) atrasados++;
            }
        }
        List<Multa> geradas = multas.listarGeradasNoPeriodo(inicio, fim);
        BigDecimal total = geradas.stream().map(Multa::getValor).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new RelatorioEmprestimos(inicio, fim, lista, ativos, atrasados, devolvidos, geradas.size(), total);
    }

    public RelatorioDoacoes doacoesPorPeriodo(LocalDate inicio, LocalDate fim) {
        validarPeriodo(inicio, fim);
        List<Doacao> lista = doacoes.listarPorPeriodo(inicio, fim);
        Map<TipoDoacao, Integer> porTipo = new EnumMap<>(TipoDoacao.class);
        Map<StatusDoacao, Integer> porStatus = new EnumMap<>(StatusDoacao.class);
        int livros = 0;
        BigDecimal arrecadado = BigDecimal.ZERO;
        for (Doacao d : lista) {
            porTipo.merge(d.getTipo(), 1, Integer::sum);
            porStatus.merge(d.getStatus(), 1, Integer::sum);
            if (d.getStatus() == StatusDoacao.ACEITA) {
                if (d instanceof DoacaoLivroFisico l) livros += l.getQuantidade();
                if (d instanceof DoacaoDinheiro m) arrecadado = arrecadado.add(m.getValor());
            }
        }
        return new RelatorioDoacoes(inicio, fim, lista, porTipo, porStatus, livros, arrecadado);
    }

    private static void validarPeriodo(LocalDate inicio, LocalDate fim) {
        if (inicio == null || fim == null || fim.isBefore(inicio)) {
            throw new ValidacaoException("Informe um período válido (a data final não pode ser anterior à inicial).");
        }
    }
}
