package br.com.bibliotecasolidaria.facade;

import br.com.bibliotecasolidaria.dto.Devolucao;
import br.com.bibliotecasolidaria.model.*;
import br.com.bibliotecasolidaria.observer.EventoBiblioteca;
import br.com.bibliotecasolidaria.observer.PublicadorEventos;
import br.com.bibliotecasolidaria.repository.ItemAcervoRepositorio;
import br.com.bibliotecasolidaria.repository.UsuarioRepositorio;
import br.com.bibliotecasolidaria.service.EmprestimoService;
import br.com.bibliotecasolidaria.service.MultaService;
import java.time.LocalDate;
import java.util.Map;

public class EmprestimoFacade {
    private final EmprestimoService emprestimoService;
    private final MultaService multaService;
    private final UsuarioRepositorio usuarioRepo;
    private final ItemAcervoRepositorio itemRepo;
    private final PublicadorEventos publicador;

    public EmprestimoFacade(EmprestimoService es, MultaService ms, UsuarioRepositorio ur,
                            ItemAcervoRepositorio ir, PublicadorEventos pub) {
        this.emprestimoService = es;
        this.multaService = ms;
        this.usuarioRepo = ur;
        this.itemRepo = ir;
        this.publicador = pub;
    }

    public Emprestimo realizarEmprestimo(Long idUsuario, Long idItem) {
        Usuario usuario = usuarioRepo.buscarPorId(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
        if (!usuario.isAtivo()) {
            throw new IllegalStateException("Usuário inativo.");
        }
        if (!multaService.listarPendentes(idUsuario).isEmpty()) {
            throw new IllegalStateException("Usuário possui multas pendentes.");
        }

        ItemAcervo item = itemRepo.buscarPorId(idItem)
                .orElseThrow(() -> new IllegalArgumentException("Item de acervo não encontrado."));
        if (!item.estaDisponivel()) {
            throw new IllegalStateException("Item do acervo não está disponível.");
        }

        item.marcarEmprestado();
        itemRepo.salvar(item);

        Emprestimo emp = new Emprestimo(null, idUsuario, idItem, LocalDate.now(),
                LocalDate.now().plusDays(14), null, 0, StatusEmprestimo.ATIVO);
        return emprestimoService.salvar(emp);
    }

    public Devolucao devolver(Long idEmprestimo) {
        Emprestimo emp = emprestimoService.buscarPorId(idEmprestimo)
                .orElseThrow(() -> new IllegalArgumentException("Empréstimo não encontrado."));

        emp.registrarDevolucao();
        emprestimoService.salvar(emp);

        ItemAcervo item = itemRepo.buscarPorId(emp.getItemAcervoId()).orElse(null);
        if (item != null) {
            item.marcarDisponivel();
            itemRepo.salvar(item);
        }

        Multa multa = null;
        if (emp.estaAtrasado()) {
            multa = multaService.gerarMulta(emp);
        }

        publicador.notificar(new EventoBiblioteca(TipoEvento.LIVRO_DISPONIVEL, null,
                "Item devolvido e liberado", Map.of("itemId", emp.getItemAcervoId())));

        return new Devolucao(emp, multa, emp.diasAtraso());
    }

    public Emprestimo renovar(Long idEmprestimo) {
        return emprestimoService.renovar(idEmprestimo);
    }

    public ComprovantePagamento pagarMulta(Long idMulta) {
        return multaService.pagarMulta(idMulta);
    }
}
