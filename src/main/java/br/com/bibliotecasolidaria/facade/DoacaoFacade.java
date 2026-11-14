package br.com.bibliotecasolidaria.facade;

import br.com.bibliotecasolidaria.dto.DoacaoDTO;
import br.com.bibliotecasolidaria.exception.NegocioException;
import br.com.bibliotecasolidaria.model.Bonificacao;
import br.com.bibliotecasolidaria.model.Doacao;
import br.com.bibliotecasolidaria.model.Doador;
import br.com.bibliotecasolidaria.model.ItemAcervo;
import br.com.bibliotecasolidaria.persistence.ExecutorTransacional;
import br.com.bibliotecasolidaria.service.AcervoService;
import br.com.bibliotecasolidaria.service.BonificacaoService;
import br.com.bibliotecasolidaria.service.DoacaoService;
import br.com.bibliotecasolidaria.service.ReservaService;
import br.com.bibliotecasolidaria.service.UsuarioService;

import java.util.List;

/**
 * Facade do fluxo de doação: valida o doador, avalia o item ou valor, inclui no acervo,
 * concede bonificações e notifica o doador (evento DOACAO_ACEITA), tudo em uma transação.
 */
public class DoacaoFacade {
    private final DoacaoService doacoes;
    private final UsuarioService usuarios;
    private final AcervoService acervo;
    private final BonificacaoService bonificacoes;
    private final ReservaService reservas;
    private final ExecutorTransacional transacao;

    public DoacaoFacade(DoacaoService doacoes, UsuarioService usuarios, AcervoService acervo,
                        BonificacaoService bonificacoes, ReservaService reservas, ExecutorTransacional transacao) {
        this.doacoes = doacoes;
        this.usuarios = usuarios;
        this.acervo = acervo;
        this.bonificacoes = bonificacoes;
        this.reservas = reservas;
        this.transacao = transacao;
    }

    /** Registra e processa a doação: ao final ela está ACEITA ou RECUSADA. */
    public Doacao registrarDoacao(DoacaoDTO dto) {
        return transacao.emTransacao(() -> avaliarDoacao(doacoes.registrar(dto).getId()));
    }

    /** Avalia uma doação pendente pelas regras do seu tipo: aceita (e processa) ou recusa. */
    public Doacao avaliarDoacao(long id) {
        return transacao.emTransacao(() -> {
            if (doacoes.avaliar(id)) {
                return concluirAceite(id);
            }
            return doacoes.recusar(id, "Não atende aos critérios mínimos de aceitação.");
        });
    }

    /** Decisão manual de um funcionário: aceita uma doação pendente, mesmo que a regra automática a recusasse. */
    public Doacao aceitarDoacao(long id) {
        return transacao.emTransacao(() -> concluirAceite(id));
    }

    public Doacao recusarDoacao(long id, String motivo) {
        return transacao.emTransacao(() -> doacoes.recusar(id, motivo));
    }

    private Doacao concluirAceite(long id) {
        Doacao d = doacoes.buscar(id);
        if (d.estaFinalizada()) {
            throw new NegocioException("A doação já foi finalizada com status " + d.getStatus() + ".");
        }
        Doador doador = usuarios.buscarDoador(d.getDoadorId());
        List<ItemAcervo> itens = acervo.incluirDoacao(d);
        itens.forEach(reservas::reservarItemParaProximo);
        List<Bonificacao> concedidas = bonificacoes.processar(doador, d);
        return doacoes.aceitar(id, concedidas);
    }
}
