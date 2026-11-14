package br.com.bibliotecasolidaria.view;

import br.com.bibliotecasolidaria.controller.Requisicao;
import br.com.bibliotecasolidaria.controller.Resposta;

import java.util.List;
import java.util.function.Function;

/** Item de menu: título, campos a perguntar e a ação do controller que processa a requisição. */
public record OpcaoMenu(String titulo, List<Campo> campos, Function<Requisicao, Resposta> acao) {
    public OpcaoMenu(String titulo, Function<Requisicao, Resposta> acao, Campo... campos) {
        this(titulo, List.of(campos), acao);
    }
}
