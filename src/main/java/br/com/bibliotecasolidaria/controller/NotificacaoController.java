package br.com.bibliotecasolidaria.controller;

import br.com.bibliotecasolidaria.model.enums.TipoEvento;
import br.com.bibliotecasolidaria.persistence.ExecutorTransacional;
import br.com.bibliotecasolidaria.service.AgendadorVencimentos;
import br.com.bibliotecasolidaria.service.NotificacaoService;

public class NotificacaoController extends BaseController {
    private final NotificacaoService notificacoes;
    private final AgendadorVencimentos agendador;
    private final ExecutorTransacional transacao;

    public NotificacaoController(NotificacaoService notificacoes, AgendadorVencimentos agendador,
                                 ExecutorTransacional transacao) {
        this.notificacoes = notificacoes;
        this.agendador = agendador;
        this.transacao = transacao;
    }

    /** Campos: usuarioId, tipo, mensagem. */
    public Resposta criar(Requisicao req) {
        return executar(() -> Resposta.criado("Notificação criada.", notificacoes.criar(req.longo("usuarioId"),
                req.enumeracao("tipo", TipoEvento.class), req.texto("mensagem"))));
    }

    public Resposta buscar(Requisicao req) {
        return executar(() -> Resposta.ok("Notificação encontrada.", notificacoes.buscar(req.longo("id"))));
    }

    public Resposta listar(Requisicao req) {
        return executar(() -> lista("notificação(ões)", notificacoes.listar()));
    }

    /** Campos: usuarioId; apenasNaoLidas (opcional, true/false). */
    public Resposta listarPorUsuario(Requisicao req) {
        return executar(() -> {
            long usuarioId = req.longo("usuarioId");
            boolean naoLidas = req.tem("apenasNaoLidas") && req.booleano("apenasNaoLidas");
            return lista("notificação(ões)", naoLidas ? notificacoes.listarNaoLidas(usuarioId)
                    : notificacoes.listarPorUsuario(usuarioId));
        });
    }

    public Resposta marcarComoLida(Requisicao req) {
        return executar(() -> Resposta.ok("Notificação marcada como lida.", notificacoes.marcarComoLida(req.longo("id"))));
    }

    /** Campos: id, mensagem. */
    public Resposta atualizar(Requisicao req) {
        return executar(() -> Resposta.ok("Notificação atualizada.",
                notificacoes.atualizarMensagem(req.longo("id"), req.texto("mensagem"))));
    }

    public Resposta remover(Requisicao req) {
        return executar(() -> {
            notificacoes.remover(req.longo("id"));
            return Resposta.ok("Notificação removida.", null);
        });
    }

    /** Executa a verificação diária: marca atrasos e avisa os empréstimos que vencem em breve. */
    public Resposta verificarVencimentos(Requisicao req) {
        return executar(() -> {
            int enviados = transacao.emTransacao(agendador::verificarVencimentosProximos);
            return Resposta.ok(enviados + " aviso(s) de vencimento enviado(s).", null);
        });
    }
}
