package br.com.bibliotecasolidaria.service;

import br.com.bibliotecasolidaria.exception.NaoEncontradoException;
import br.com.bibliotecasolidaria.exception.ValidacaoException;
import br.com.bibliotecasolidaria.model.Bonificacao;
import br.com.bibliotecasolidaria.model.Doacao;
import br.com.bibliotecasolidaria.model.Doador;
import br.com.bibliotecasolidaria.model.enums.TipoBonificacao;
import br.com.bibliotecasolidaria.persistence.BonificacaoRepositorio;
import br.com.bibliotecasolidaria.strategy.BonificacaoStrategy;
import br.com.bibliotecasolidaria.util.Textos;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Context do Strategy de bonificação. */
public class BonificacaoService {
    private final BonificacaoRepositorio bonificacoes;
    private final UsuarioService usuarios;
    private final List<BonificacaoStrategy> estrategias;
    private final Clock clock;

    public BonificacaoService(BonificacaoRepositorio bonificacoes, UsuarioService usuarios,
                              List<BonificacaoStrategy> estrategias, Clock clock) {
        this.bonificacoes = bonificacoes;
        this.usuarios = usuarios;
        this.estrategias = List.copyOf(estrategias);
        this.clock = clock;
    }

    /** Aplica todas as estratégias à doação aceita e persiste as bonificações merecidas. */
    public List<Bonificacao> processar(Doador doador, Doacao doacao) {
        List<Bonificacao> concedidas = new ArrayList<>();
        for (BonificacaoStrategy s : estrategias) {
            s.avaliar(doador, doacao).ifPresent(b -> concedidas.add(bonificacoes.salvar(b)));
        }
        return concedidas;
    }

    /** Concessão manual. */
    public Bonificacao conceder(long doadorId, TipoBonificacao tipo, String descricao) {
        usuarios.buscarDoador(doadorId);
        if (tipo == null) throw new ValidacaoException("O tipo da bonificação é obrigatório.");
        if (Textos.emBranco(descricao)) throw new ValidacaoException("A descrição é obrigatória.");
        return bonificacoes.salvar(new Bonificacao(null, doadorId, tipo, descricao.trim(), LocalDate.now(clock)));
    }

    public Bonificacao buscar(long id) {
        return bonificacoes.buscarPorId(id).orElseThrow(() -> new NaoEncontradoException("Bonificação " + id + " não encontrada."));
    }

    public List<Bonificacao> listar() { return bonificacoes.listarTodos(); }

    public List<Bonificacao> listarPorDoador(long doadorId) { return bonificacoes.listarPorDoador(doadorId); }

    public Bonificacao atualizar(long id, TipoBonificacao tipo, String descricao) {
        Bonificacao b = buscar(id);
        if (tipo != null) b.setTipo(tipo);
        if (!Textos.emBranco(descricao)) b.setDescricao(descricao.trim());
        return bonificacoes.salvar(b);
    }

    public void remover(long id) {
        buscar(id);
        bonificacoes.remover(id);
    }
}
