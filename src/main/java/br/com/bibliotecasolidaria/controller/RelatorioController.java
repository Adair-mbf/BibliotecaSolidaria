package br.com.bibliotecasolidaria.controller;

import br.com.bibliotecasolidaria.service.RelatorioService;

public class RelatorioController extends BaseController {
    private final RelatorioService relatorios;

    public RelatorioController(RelatorioService relatorios) {
        this.relatorios = relatorios;
    }

    /** Campos: inicio, fim (AAAA-MM-DD). */
    public Resposta emprestimosPorPeriodo(Requisicao req) {
        return executar(() -> Resposta.ok("Relatório gerado.",
                relatorios.emprestimosPorPeriodo(req.data("inicio"), req.data("fim"))));
    }

    /** Campos: inicio, fim (AAAA-MM-DD). */
    public Resposta doacoesPorPeriodo(Requisicao req) {
        return executar(() -> Resposta.ok("Relatório gerado.",
                relatorios.doacoesPorPeriodo(req.data("inicio"), req.data("fim"))));
    }
}
