package br.com.bibliotecasolidaria.service;

import br.com.bibliotecasolidaria.exception.ConflitoException;
import br.com.bibliotecasolidaria.exception.NaoEncontradoException;
import br.com.bibliotecasolidaria.exception.NegocioException;
import br.com.bibliotecasolidaria.model.ExemplarFisico;
import br.com.bibliotecasolidaria.model.ItemAcervo;
import br.com.bibliotecasolidaria.model.Livro;
import br.com.bibliotecasolidaria.model.Reserva;
import br.com.bibliotecasolidaria.model.Usuario;
import br.com.bibliotecasolidaria.model.enums.StatusItem;
import br.com.bibliotecasolidaria.model.enums.StatusReserva;
import br.com.bibliotecasolidaria.model.enums.TipoEvento;
import br.com.bibliotecasolidaria.observer.EventoBiblioteca;
import br.com.bibliotecasolidaria.observer.PublicadorEventos;
import br.com.bibliotecasolidaria.persistence.ReservaRepositorio;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ReservaService {
    private final ReservaRepositorio reservas;
    private final UsuarioService usuarios;
    private final AcervoService acervo;
    private final PublicadorEventos eventos;
    private final Clock clock;

    public ReservaService(ReservaRepositorio reservas, UsuarioService usuarios, AcervoService acervo,
                          PublicadorEventos eventos, Clock clock) {
        this.reservas = reservas;
        this.usuarios = usuarios;
        this.acervo = acervo;
        this.eventos = eventos;
        this.clock = clock;
    }

    /** Entra na fila de um livro que não tem nenhum item disponível. */
    public Reserva criar(long usuarioId, long livroId) {
        if (!usuarios.buscarUsuarioSite(usuarioId).podeEmprestar()) {
            throw new NegocioException("Usuário inativo não pode reservar.");
        }
        acervo.buscarLivro(livroId);
        if (acervo.temItemDisponivel(livroId)) {
            throw new NegocioException("Há item disponível para este livro; faça o empréstimo em vez de reservar.");
        }
        if (reservas.buscarAtiva(usuarioId, livroId).isPresent()) {
            throw new ConflitoException("O usuário já possui uma reserva ativa para este livro.");
        }
        return reservas.salvar(new Reserva(null, usuarioId, livroId, LocalDate.now(clock), StatusReserva.ATIVA));
    }

    public Reserva buscar(long id) {
        return reservas.buscarPorId(id).orElseThrow(() -> new NaoEncontradoException("Reserva " + id + " não encontrada."));
    }

    public List<Reserva> listar() { return reservas.listarTodos(); }

    public List<Reserva> listarPorUsuario(long usuarioId) { return reservas.listarPorUsuario(usuarioId); }

    public List<Reserva> listarAtivasPorLivro(long livroId) { return reservas.listarAtivasPorLivro(livroId); }

    public Optional<Reserva> proximaDaFila(long livroId) { return reservas.proximaDaFila(livroId); }

    public boolean existeFilaParaLivro(long livroId) { return reservas.proximaDaFila(livroId).isPresent(); }

    public Reserva cancelar(long id) {
        Reserva r = buscar(id);
        r.cancelar();
        reservas.salvar(r);
        liberarItemReservadoSemFila(r.getLivroId());
        return r;
    }

    public Reserva atender(long id) {
        Reserva r = buscar(id);
        r.atender();
        return reservas.salvar(r);
    }

    /** Atende a reserva ativa do usuário para o livro, se existir (usado quando ele retira o item). */
    public void atenderReservaDoUsuario(long usuarioId, long livroId) {
        reservas.buscarAtiva(usuarioId, livroId).ifPresent(r -> {
            r.atender();
            reservas.salvar(r);
        });
    }

    public void remover(long id) {
        Reserva r = buscar(id);
        reservas.remover(id);
        if (r.getStatus() == StatusReserva.ATIVA) {
            liberarItemReservadoSemFila(r.getLivroId());
        }
    }

    /**
     * Se o livro tem fila, segura o exemplar para o primeiro da fila e o avisa (evento LIVRO_DISPONIVEL).
     * E-books não têm fila. Retorna {@code true} se o item foi reservado.
     */
    public boolean reservarItemParaProximo(ItemAcervo item) {
        if (!(item instanceof ExemplarFisico)) {
            return false;
        }
        Optional<Reserva> proxima = reservas.proximaDaFila(item.getLivroId());
        if (proxima.isEmpty()) {
            return false;
        }
        acervo.marcarReservado(item.getId());
        Usuario destinatario = usuarios.buscar(proxima.get().getUsuarioId());
        Livro livro = acervo.buscarLivro(item.getLivroId());
        eventos.notificar(new EventoBiblioteca(TipoEvento.LIVRO_DISPONIVEL, destinatario,
                Map.of("livroTitulo", livro.getTitulo(), "livroId", livro.getId(), "itemId", item.getId()),
                LocalDateTime.now(clock)));
        return true;
    }

    /** Se um item ficou reservado e a fila esvaziou (cancelamento), devolve-o ao estado disponível. */
    private void liberarItemReservadoSemFila(long livroId) {
        if (existeFilaParaLivro(livroId)) {
            return;
        }
        for (ItemAcervo item : acervo.listarItensPorLivro(livroId)) {
            if (item instanceof ExemplarFisico && item.getStatus() == StatusItem.RESERVADO) {
                acervo.marcarDisponivel(item.getId());
            }
        }
    }
}
