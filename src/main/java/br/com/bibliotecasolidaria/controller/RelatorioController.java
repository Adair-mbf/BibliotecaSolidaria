package br.com.bibliotecasolidaria.controller;

import br.com.bibliotecasolidaria.service.RelatorioService;
import java.util.Map;

public class RelatorioController {
    private final RelatorioService service;

    public RelatorioController(RelatorioService service) {
        this.service = service;
    }

    public record Resposta<T>(int status, String mensagem, T dados) {}

    public Resposta<Map<String, Object>> emprestimosPorPeriodo() {
        return new Resposta<>(200, "Relatório gerado", service.gerarRelatorioGeral());
    }

    public Resposta<Map<String, Object>> doacoes() {
        return new Resposta<>(200, "Relatório de doações", service.gerarRelatorioGeral());
    }
}
