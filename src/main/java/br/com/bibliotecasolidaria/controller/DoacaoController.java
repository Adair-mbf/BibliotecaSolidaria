package br.com.bibliotecasolidaria.controller;

import br.com.bibliotecasolidaria.dto.DoacaoDTO;
import br.com.bibliotecasolidaria.facade.DoacaoFacade;
import br.com.bibliotecasolidaria.model.Doacao;
import br.com.bibliotecasolidaria.model.enums.StatusDoacao;
import br.com.bibliotecasolidaria.model.enums.TipoDoacao;
import br.com.bibliotecasolidaria.service.DoacaoService;

import java.util.HashMap;
import java.util.Map;

public class DoacaoController extends BaseController {
    private static final String[] CAMPOS_ITEM = {"titulo", "autor", "isbn", "categoria", "estado", "quantidade",
            "arquivoUrl", "formato", "valor", "meioPagamento", "comprovante"};

    private final DoacaoFacade facade;
    private final DoacaoService doacoes;

    public DoacaoController(DoacaoFacade facade, DoacaoService doacoes) {
        this.facade = facade;
        this.doacoes = doacoes;
    }

    /**
     * Registra e processa a doação. Campos: idDoador, tipo e os dados do item (LIVRO_FISICO: titulo, autor, isbn,
     * categoria, estado, quantidade; EBOOK: titulo, autor, arquivoUrl, formato; DINHEIRO: valor, meioPagamento,
     * comprovante).
     */
    public Resposta registrar(Requisicao req) {
        return executar(() -> {
            Doacao d = facade.registrarDoacao(new DoacaoDTO(req.longo("idDoador"),
                    req.enumeracao("tipo", TipoDoacao.class), dadosItem(req)));
            return Resposta.criado(d.getStatus() == StatusDoacao.ACEITA ? "Doação aceita. Obrigado!"
                    : "Doação registrada, mas recusada na avaliação.", d);
        });
    }

    /** Reavalia uma doação pendente pelas regras do tipo (aceita ou recusa). */
    public Resposta avaliar(Requisicao req) {
        return executar(() -> Resposta.ok("Avaliação concluída.", facade.avaliarDoacao(req.longo("id"))));
    }

    /** Decisão manual de aceite de uma doação pendente. */
    public Resposta aceitar(Requisicao req) {
        return executar(() -> Resposta.ok("Doação aceita.", facade.aceitarDoacao(req.longo("id"))));
    }

    /** Campos: id, motivo. */
    public Resposta recusar(Requisicao req) {
        return executar(() -> Resposta.ok("Doação recusada.", facade.recusarDoacao(req.longo("id"), req.texto("motivo"))));
    }

    public Resposta buscar(Requisicao req) {
        return executar(() -> Resposta.ok("Doação encontrada.", doacoes.buscar(req.longo("id"))));
    }

    /** Filtro opcional: status. */
    public Resposta listar(Requisicao req) {
        return executar(() -> {
            StatusDoacao status = req.enumeracaoOpcional("status", StatusDoacao.class);
            return lista("doação(ões)", status == null ? doacoes.listar() : doacoes.listarPorStatus(status));
        });
    }

    public Resposta listarPorDoador(Requisicao req) {
        return executar(() -> lista("doação(ões)", doacoes.listarPorDoador(req.longo("idDoador"))));
    }

    /** Campos: id + dados do item a alterar (somente doações não finalizadas). */
    public Resposta atualizar(Requisicao req) {
        return executar(() -> Resposta.ok("Doação atualizada.", doacoes.atualizar(req.longo("id"), dadosItem(req))));
    }

    public Resposta remover(Requisicao req) {
        return executar(() -> {
            doacoes.remover(req.longo("id"));
            return Resposta.ok("Doação removida.", null);
        });
    }

    private static Map<String, Object> dadosItem(Requisicao req) {
        Map<String, Object> dados = new HashMap<>();
        for (String c : CAMPOS_ITEM) {
            if (req.tem(c)) {
                dados.put(c, req.texto(c));
            }
        }
        return dados;
    }
}
