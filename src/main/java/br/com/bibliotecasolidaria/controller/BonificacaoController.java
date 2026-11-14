package br.com.bibliotecasolidaria.controller;

import br.com.bibliotecasolidaria.model.enums.TipoBonificacao;
import br.com.bibliotecasolidaria.service.BonificacaoService;

/** As bonificações automáticas são concedidas no fluxo de doação; aqui ficam consulta e concessão manual. */
public class BonificacaoController extends BaseController {
    private final BonificacaoService bonificacoes;

    public BonificacaoController(BonificacaoService bonificacoes) {
        this.bonificacoes = bonificacoes;
    }

    /** Campos: doadorId, tipo, descricao. */
    public Resposta conceder(Requisicao req) {
        return executar(() -> Resposta.criado("Bonificação concedida.", bonificacoes.conceder(req.longo("doadorId"),
                req.enumeracao("tipo", TipoBonificacao.class), req.texto("descricao"))));
    }

    public Resposta buscar(Requisicao req) {
        return executar(() -> Resposta.ok("Bonificação encontrada.", bonificacoes.buscar(req.longo("id"))));
    }

    public Resposta listar(Requisicao req) {
        return executar(() -> lista("bonificação(ões)", bonificacoes.listar()));
    }

    public Resposta listarPorDoador(Requisicao req) {
        return executar(() -> lista("bonificação(ões)", bonificacoes.listarPorDoador(req.longo("doadorId"))));
    }

    /** Campos: id + tipo e/ou descricao. */
    public Resposta atualizar(Requisicao req) {
        return executar(() -> Resposta.ok("Bonificação atualizada.", bonificacoes.atualizar(req.longo("id"),
                req.enumeracaoOpcional("tipo", TipoBonificacao.class), req.textoOpcional("descricao"))));
    }

    public Resposta remover(Requisicao req) {
        return executar(() -> {
            bonificacoes.remover(req.longo("id"));
            return Resposta.ok("Bonificação removida.", null);
        });
    }
}
