package br.com.bibliotecasolidaria.controller;

import br.com.bibliotecasolidaria.service.MultaService;

/** O pagamento de multas fica em {@link EmprestimoController#pagarMulta}, conforme o diagrama. */
public class MultaController extends BaseController {
    private final MultaService multas;

    public MultaController(MultaService multas) {
        this.multas = multas;
    }

    /** Campos: emprestimoId, valor. */
    public Resposta criar(Requisicao req) {
        return executar(() -> Resposta.criado("Multa criada.",
                multas.criar(req.longo("emprestimoId"), req.decimal("valor"))));
    }

    public Resposta buscar(Requisicao req) {
        return executar(() -> Resposta.ok("Multa encontrada.", multas.buscar(req.longo("id"))));
    }

    public Resposta listar(Requisicao req) {
        return executar(() -> lista("multa(s)", multas.listar()));
    }

    public Resposta listarPendentes(Requisicao req) {
        return executar(() -> lista("multa(s) pendente(s)", multas.listarPendentes(req.longo("usuarioId"))));
    }

    /** Campos: id, valor (multa pendente). */
    public Resposta atualizar(Requisicao req) {
        return executar(() -> Resposta.ok("Valor da multa atualizado.",
                multas.atualizarValor(req.longo("id"), req.decimal("valor"))));
    }

    /** Cancela (perdoa) uma multa pendente. */
    public Resposta remover(Requisicao req) {
        return executar(() -> {
            multas.remover(req.longo("id"));
            return Resposta.ok("Multa removida.", null);
        });
    }
}
