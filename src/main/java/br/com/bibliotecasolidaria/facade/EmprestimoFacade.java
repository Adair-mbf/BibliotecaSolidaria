package br.com.bibliotecasolidaria.facade;

import br.com.bibliotecasolidaria.dto.Devolucao;
import br.com.bibliotecasolidaria.exception.NegocioException;
import br.com.bibliotecasolidaria.model.Emprestimo;
import br.com.bibliotecasolidaria.model.ComprovantePagamento;
import br.com.bibliotecasolidaria.model.ItemAcervo;
import br.com.bibliotecasolidaria.model.Multa;
import br.com.bibliotecasolidaria.model.Reserva;
import br.com.bibliotecasolidaria.model.UsuarioSite;
import br.com.bibliotecasolidaria.model.enums.StatusItem;
import br.com.bibliotecasolidaria.persistence.ExecutorTransacional;
import br.com.bibliotecasolidaria.service.AcervoService;
import br.com.bibliotecasolidaria.service.EmprestimoService;
import br.com.bibliotecasolidaria.service.MultaService;
import br.com.bibliotecasolidaria.service.ReservaService;
import br.com.bibliotecasolidaria.service.UsuarioService;

/** Facade: orquestra, de forma atômica, os passos de empréstimo, devolução, renovação e pagamento de multa. */
public class EmprestimoFacade {
    private final UsuarioService usuarios;
    private final AcervoService acervo;
    private final EmprestimoService emprestimos;
    private final MultaService multas;
    private final ReservaService reservas;
    private final ExecutorTransacional transacao;

    public EmprestimoFacade(UsuarioService usuarios, AcervoService acervo, EmprestimoService emprestimos,
                            MultaService multas, ReservaService reservas, ExecutorTransacional transacao) {
        this.usuarios = usuarios;
        this.acervo = acervo;
        this.emprestimos = emprestimos;
        this.multas = multas;
        this.reservas = reservas;
        this.transacao = transacao;
    }

    /** 1) usuário ativo e sem multa pendente; 2) item disponível; 3) registra e marca o item; 4) atende a reserva. */
    public Emprestimo emprestar(long usuarioId, long itemId) {
        return transacao.emTransacao(() -> {
            UsuarioSite usuario = usuarios.buscarUsuarioSite(usuarioId);
            if (!usuario.podeEmprestar()) {
                throw new NegocioException("Usuário inativo não pode tomar empréstimos.");
            }
            if (multas.possuiPendencias(usuarioId)) {
                throw new NegocioException("O usuário possui multas pendentes; quite-as antes de emprestar.");
            }
            ItemAcervo item = acervo.buscarItem(itemId);
            if (!disponivelPara(item, usuarioId)) {
                throw new NegocioException("O item " + itemId + " não está disponível (status " + item.getStatus() + ").");
            }
            Emprestimo emprestimo = emprestimos.registrarEmprestimo(usuario, item);
            reservas.atenderReservaDoUsuario(usuarioId, item.getLivroId());
            return emprestimo;
        });
    }

    /** Devolve o item, libera-o (ou o segura para a fila) e gera multa se houve atraso. */
    public Devolucao devolver(long emprestimoId) {
        return transacao.emTransacao(() -> {
            Emprestimo e = emprestimos.registrarDevolucao(emprestimoId);
            int dias = e.diasAtraso(e.getDataDevolucao());
            Multa multa = dias > 0 ? multas.gerarMulta(e) : null;
            return new Devolucao(e, multa, dias);
        });
    }

    public Emprestimo renovar(long emprestimoId) {
        return transacao.emTransacao(() -> emprestimos.renovar(emprestimoId));
    }

    public ComprovantePagamento pagarMulta(long multaId) {
        return transacao.emTransacao(() -> multas.pagarMulta(multaId));
    }

    /** Item livre, ou segurado (RESERVADO) para este mesmo usuário por ser o primeiro da fila. */
    private boolean disponivelPara(ItemAcervo item, long usuarioId) {
        if (item.estaDisponivel()) {
            return true;
        }
        if (item.getStatus() == StatusItem.RESERVADO) {
            return reservas.proximaDaFila(item.getLivroId()).map(Reserva::getUsuarioId)
                    .filter(id -> id == usuarioId).isPresent();
        }
        return false;
    }
}
