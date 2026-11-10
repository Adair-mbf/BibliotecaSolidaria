package br.com.bibliotecasolidaria.controller;

import br.com.bibliotecasolidaria.dto.DoacaoDTO;
import br.com.bibliotecasolidaria.facade.DoacaoFacade;
import br.com.bibliotecasolidaria.model.Doacao;
import br.com.bibliotecasolidaria.service.DoacaoService;

public class DoacaoController {
    private final DoacaoFacade facade;
    private final DoacaoService service;

    public DoacaoController(DoacaoFacade facade, DoacaoService service) {
        this.facade = facade;
        this.service = service;
    }

    public record Resposta<T>(int status, String mensagem, T dados) {}

    public Resposta<Doacao> registrar(DoacaoDTO req) {
        try {
            Doacao d = facade.registrarDoacao(req);
            return new Resposta<>(201, "Doação registrada", d);
        } catch (Exception e) {
            return new Resposta<>(400, e.getMessage(), null);
        }
    }

    public Resposta<Void> avaliar(Long id) {
        try {
            service.aceitar(id);
            return new Resposta<>(200, "Doação avaliada e aceita", null);
        } catch (Exception e) {
            return new Resposta<>(400, e.getMessage(), null);
        }
    }
}
