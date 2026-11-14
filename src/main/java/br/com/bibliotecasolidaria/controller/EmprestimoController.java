package br.com.bibliotecasolidaria.controller;

import br.com.bibliotecasolidaria.dto.Devolucao;
import br.com.bibliotecasolidaria.facade.EmprestimoFacade;
import br.com.bibliotecasolidaria.model.ComprovantePagamento;
import br.com.bibliotecasolidaria.service.EmprestimoService;

public class EmprestimoController extends BaseController {
    private final EmprestimoFacade facade;
    private final EmprestimoService emprestimos;

    public EmprestimoController(EmprestimoFacade facade, EmprestimoService emprestimos) {
        this.facade = facade;
        this.emprestimos = emprestimos;
    }

    /** Campos: usuarioId, itemId. */
    public Resposta emprestar(Requisicao req) {
        return executar(() -> Resposta.criado("Empréstimo registrado.",
                facade.emprestar(req.longo("usuarioId"), req.longo("itemId"))));
    }

    /** Campo: id do empréstimo. */
    public Resposta devolver(Requisicao req) {
        return executar(() -> {
            Devolucao d = facade.devolver(req.longo("id"));
            return Resposta.ok(d.geroumulta() ? "Devolução registrada com atraso de " + d.diasAtraso()
                    + " dia(s); multa gerada." : "Devolução registrada.", d);
        });
    }

    public Resposta renovar(Requisicao req) {
        return executar(() -> Resposta.ok("Empréstimo renovado.", facade.renovar(req.longo("id"))));
    }

    /** Campo: id da multa. A multa só é quitada se o pagamento for aprovado. */
    public Resposta pagarMulta(Requisicao req) {
        return executar(() -> {
            ComprovantePagamento c = facade.pagarMulta(req.longo("id"));
            return c.aprovado() ? Resposta.ok("Multa paga.", c)
                    : Resposta.erro(422, "Pagamento não aprovado (status " + c.status() + "); a multa continua pendente.");
        });
    }

    public Resposta buscar(Requisicao req) {
        return executar(() -> Resposta.ok("Empréstimo encontrado.", emprestimos.buscar(req.longo("id"))));
    }

    public Resposta listar(Requisicao req) {
        return executar(() -> lista("empréstimo(s)", emprestimos.listar()));
    }

    public Resposta listarPorUsuario(Requisicao req) {
        return executar(() -> lista("empréstimo(s)", emprestimos.listarPorUsuario(req.longo("usuarioId"))));
    }

    public Resposta listarAtrasados(Requisicao req) {
        return executar(() -> lista("empréstimo(s) em atraso", emprestimos.listarAtrasados()));
    }

    /** Campos: id, dataPrevistaDevolucao (AAAA-MM-DD). */
    public Resposta ajustarPrazo(Requisicao req) {
        return executar(() -> Resposta.ok("Prazo ajustado.",
                emprestimos.ajustarPrazo(req.longo("id"), req.data("dataPrevistaDevolucao"))));
    }

    /** Marca como ATRASADO os empréstimos ativos com prazo vencido. */
    public Resposta sincronizarAtrasos(Requisicao req) {
        return executar(() -> Resposta.ok(emprestimos.atualizarStatusAtrasados() + " empréstimo(s) marcado(s) como atrasado(s).", null));
    }

    public Resposta remover(Requisicao req) {
        return executar(() -> {
            emprestimos.remover(req.longo("id"));
            return Resposta.ok("Empréstimo removido.", null);
        });
    }
}
