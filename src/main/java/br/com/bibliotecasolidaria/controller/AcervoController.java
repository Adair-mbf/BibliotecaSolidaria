package br.com.bibliotecasolidaria.controller;

import br.com.bibliotecasolidaria.model.ItemAcervo;
import br.com.bibliotecasolidaria.model.Livro;
import br.com.bibliotecasolidaria.service.AcervoService;
import java.util.List;

public class AcervoController {
    private final AcervoService service;

    public AcervoController(AcervoService service) {
        this.service = service;
    }

    public record Resposta<T>(int status, String mensagem, T dados) {}

    public Resposta<List<ItemAcervo>> consultarDisponiveis() {
        return new Resposta<>(200, "Itens disponíveis consultados", service.listarDisponiveis());
    }

    public Resposta<Livro> cadastrarLivro(Livro req) {
        try {
            Livro l = service.cadastrarLivro(req);
            return new Resposta<>(201, "Livro cadastrado", l);
        } catch (Exception e) {
            return new Resposta<>(400, e.getMessage(), null);
        }
    }
}
