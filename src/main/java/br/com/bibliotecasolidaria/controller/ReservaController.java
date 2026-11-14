package br.com.bibliotecasolidaria.controller;

import br.com.bibliotecasolidaria.persistence.ExecutorTransacional;
import br.com.bibliotecasolidaria.service.ReservaService;

public class ReservaController extends BaseController {
    private final ReservaService reservas;
    private final ExecutorTransacional transacao;

    public ReservaController(ReservaService reservas, ExecutorTransacional transacao) {
        this.reservas = reservas;
        this.transacao = transacao;
    }

    /** Campos: usuarioId, livroId. */
    public Resposta criar(Requisicao req) {
        return executar(() -> Resposta.criado("Reserva criada.",
                reservas.criar(req.longo("usuarioId"), req.longo("livroId"))));
    }

    public Resposta buscar(Requisicao req) {
        return executar(() -> Resposta.ok("Reserva encontrada.", reservas.buscar(req.longo("id"))));
    }

    public Resposta listar(Requisicao req) {
        return executar(() -> lista("reserva(s)", reservas.listar()));
    }

    public Resposta listarPorUsuario(Requisicao req) {
        return executar(() -> lista("reserva(s)", reservas.listarPorUsuario(req.longo("usuarioId"))));
    }

    public Resposta proximaDaFila(Requisicao req) {
        return executar(() -> reservas.proximaDaFila(req.longo("livroId"))
                .map(r -> Resposta.ok("Próxima reserva da fila.", r))
                .orElseGet(() -> Resposta.erro(404, "Não há reservas ativas para este livro.")));
    }

    public Resposta cancelar(Requisicao req) {
        return executar(() -> Resposta.ok("Reserva cancelada.",
                transacao.emTransacao(() -> reservas.cancelar(req.longo("id")))));
    }

    public Resposta atender(Requisicao req) {
        return executar(() -> Resposta.ok("Reserva atendida.", reservas.atender(req.longo("id"))));
    }

    public Resposta remover(Requisicao req) {
        return executar(() -> {
            transacao.emTransacao(() -> reservas.remover(req.longo("id")));
            return Resposta.ok("Reserva removida.", null);
        });
    }
}
