package br.com.bibliotecasolidaria.service;

import br.com.bibliotecasolidaria.exception.NaoEncontradoException;
import br.com.bibliotecasolidaria.exception.ValidacaoException;
import br.com.bibliotecasolidaria.model.Notificacao;
import br.com.bibliotecasolidaria.model.enums.TipoEvento;
import br.com.bibliotecasolidaria.persistence.NotificacaoRepositorio;
import br.com.bibliotecasolidaria.util.Textos;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

public class NotificacaoService {
    private final NotificacaoRepositorio notificacoes;
    private final UsuarioService usuarios;
    private final Clock clock;

    public NotificacaoService(NotificacaoRepositorio notificacoes, UsuarioService usuarios, Clock clock) {
        this.notificacoes = notificacoes;
        this.usuarios = usuarios;
        this.clock = clock;
    }

    /** Criação manual de um aviso. */
    public Notificacao criar(long usuarioId, TipoEvento tipo, String mensagem) {
        usuarios.buscar(usuarioId);
        if (tipo == null) throw new ValidacaoException("O tipo da notificação é obrigatório.");
        if (Textos.emBranco(mensagem)) throw new ValidacaoException("A mensagem é obrigatória.");
        return notificacoes.salvar(new Notificacao(null, usuarioId, tipo, mensagem.trim(),
                LocalDateTime.now(clock), false));
    }

    public Notificacao buscar(long id) {
        return notificacoes.buscarPorId(id).orElseThrow(() -> new NaoEncontradoException("Notificação " + id + " não encontrada."));
    }

    public List<Notificacao> listar() { return notificacoes.listarTodos(); }

    public List<Notificacao> listarPorUsuario(long usuarioId) { return notificacoes.listarPorUsuario(usuarioId); }

    public List<Notificacao> listarNaoLidas(long usuarioId) { return notificacoes.listarNaoLidas(usuarioId); }

    public Notificacao marcarComoLida(long id) {
        Notificacao n = buscar(id);
        n.marcarComoLida();
        return notificacoes.salvar(n);
    }

    public Notificacao atualizarMensagem(long id, String mensagem) {
        if (Textos.emBranco(mensagem)) throw new ValidacaoException("A mensagem é obrigatória.");
        Notificacao n = buscar(id);
        n.setMensagem(mensagem.trim());
        return notificacoes.salvar(n);
    }

    public void remover(long id) {
        buscar(id);
        notificacoes.remover(id);
    }
}
