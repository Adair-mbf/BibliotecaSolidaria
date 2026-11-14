package br.com.bibliotecasolidaria.facade;

import br.com.bibliotecasolidaria.adapter.GatewayPagamento;
import br.com.bibliotecasolidaria.dto.Devolucao;
import br.com.bibliotecasolidaria.exception.ConflitoException;
import br.com.bibliotecasolidaria.exception.NaoEncontradoException;
import br.com.bibliotecasolidaria.exception.NegocioException;
import br.com.bibliotecasolidaria.model.*;
import br.com.bibliotecasolidaria.model.enums.*;
import br.com.bibliotecasolidaria.service.MultaService;
import br.com.bibliotecasolidaria.strategy.MultaFixaDiaria;
import br.com.bibliotecasolidaria.support.BancoTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EmprestimoFacadeTest extends BancoTestBase {
    private UsuarioSite ana;
    private UsuarioSite beto;
    private Livro livro;
    private ExemplarFisico exemplar;

    @BeforeEach
    void cenario() {
        ana = novoUsuarioSite("ana@x.com");
        beto = novoUsuarioSite("beto@x.com");
        livro = novoLivro("Dom Casmurro", "111");
        exemplar = novoExemplar(livro.getId(), "T1");
    }

    private StatusItem statusDoExemplar() {
        return ctx.acervoService.buscarItem(exemplar.getId()).getStatus();
    }

    // ------------------------------------------------------------ empréstimo

    @Test
    void emprestaMarcaOItemEDefinePrazoDeQuatorzeDias() {
        Emprestimo e = ctx.emprestimoFacade.emprestar(ana.getId(), exemplar.getId());
        assertEquals(StatusEmprestimo.ATIVO, e.getStatus());
        assertEquals(hoje(), e.getDataEmprestimo());
        assertEquals(hoje().plusDays(14), e.getDataPrevistaDevolucao());
        assertEquals(StatusItem.EMPRESTADO, statusDoExemplar());
    }

    @Test
    void naoEmprestaItemJaEmprestado() {
        ctx.emprestimoFacade.emprestar(ana.getId(), exemplar.getId());
        assertThrows(NegocioException.class, () -> ctx.emprestimoFacade.emprestar(beto.getId(), exemplar.getId()));
    }

    @Test
    void soUsuarioDoSiteAtivoPodeTomarEmprestimo() {
        Doador doador = novoDoador("d@x.com", false);
        assertThrows(NegocioException.class, () -> ctx.emprestimoFacade.emprestar(doador.getId(), exemplar.getId()));
        ctx.usuarioService.definirAtivo(ana.getId(), false);
        assertThrows(NegocioException.class, () -> ctx.emprestimoFacade.emprestar(ana.getId(), exemplar.getId()));
        assertThrows(NaoEncontradoException.class, () -> ctx.emprestimoFacade.emprestar(999L, exemplar.getId()));
        assertThrows(NaoEncontradoException.class, () -> ctx.emprestimoFacade.emprestar(beto.getId(), 999L));
        assertEquals(StatusItem.DISPONIVEL, statusDoExemplar(), "falhas não podem alterar o item");
    }

    @Test
    void ebookPodeSerEmprestadoPorVariosUsuariosAoMesmoTempo() {
        Ebook eb = (Ebook) ctx.acervoService.cadastrarItem(new Ebook(null, livro.getId(), StatusItem.DISPONIVEL, "http://x/a.pdf", "PDF"));
        ctx.emprestimoFacade.emprestar(ana.getId(), eb.getId());
        ctx.emprestimoFacade.emprestar(beto.getId(), eb.getId());
        assertEquals(2, ctx.emprestimoRepositorio.listarEmAbertoPorItem(eb.getId()).size());
        assertEquals(StatusItem.DISPONIVEL, ctx.acervoService.buscarItem(eb.getId()).getStatus());
    }

    // -------------------------------------------------------------- devolução

    @Test
    void devolucaoNoPrazoLiberaOItemSemMulta() {
        Emprestimo e = ctx.emprestimoFacade.emprestar(ana.getId(), exemplar.getId());
        relogio.avancarDias(14);
        Devolucao d = ctx.emprestimoFacade.devolver(e.getId());
        assertNull(d.multa());
        assertEquals(0, d.diasAtraso());
        assertEquals(StatusEmprestimo.DEVOLVIDO, d.emprestimo().getStatus());
        assertEquals(StatusItem.DISPONIVEL, statusDoExemplar());
    }

    @Test
    void devolucaoAtrasadaGeraMultaPeloValorDiario() {
        Emprestimo e = ctx.emprestimoFacade.emprestar(ana.getId(), exemplar.getId());
        relogio.avancarDias(20); // 6 dias além do prazo
        Devolucao d = ctx.emprestimoFacade.devolver(e.getId());
        assertEquals(6, d.diasAtraso());
        assertNotNull(d.multa());
        assertEquals(new BigDecimal("6.00"), d.multa().getValor());
        assertEquals(StatusMulta.PENDENTE, d.multa().getStatus());
        assertTrue(ctx.multaService.possuiPendencias(ana.getId()));
    }

    @Test
    void naoDevolveDuasVezes() {
        Emprestimo e = ctx.emprestimoFacade.emprestar(ana.getId(), exemplar.getId());
        ctx.emprestimoFacade.devolver(e.getId());
        assertThrows(NegocioException.class, () -> ctx.emprestimoFacade.devolver(e.getId()));
        assertThrows(NaoEncontradoException.class, () -> ctx.emprestimoFacade.devolver(999L));
    }

    // ------------------------------------------------------------------ multas

    @Test
    void multaPendenteBloqueiaNovosEmprestimosAtePagar() {
        Emprestimo e = ctx.emprestimoFacade.emprestar(ana.getId(), exemplar.getId());
        relogio.avancarDias(16);
        Multa multa = ctx.emprestimoFacade.devolver(e.getId()).multa();
        assertThrows(NegocioException.class, () -> ctx.emprestimoFacade.emprestar(ana.getId(), exemplar.getId()));

        ComprovantePagamento c = ctx.emprestimoFacade.pagarMulta(multa.getId());
        assertTrue(c.aprovado());
        assertEquals(StatusMulta.PAGA, ctx.multaService.buscar(multa.getId()).getStatus());
        assertEquals(hoje(), ctx.multaService.buscar(multa.getId()).getDataPagamento());
        assertDoesNotThrow(() -> ctx.emprestimoFacade.emprestar(ana.getId(), exemplar.getId()));
        assertThrows(NegocioException.class, () -> ctx.emprestimoFacade.pagarMulta(multa.getId()), "já paga");
    }

    @Test
    void multaContinuaPendenteQuandoOGatewayRecusa() {
        Emprestimo e = ctx.emprestimoFacade.emprestar(ana.getId(), exemplar.getId());
        relogio.avancarDias(16);
        Multa multa = ctx.emprestimoFacade.devolver(e.getId()).multa();
        GatewayPagamento recusa = new GatewayPagamento() {
            @Override public ComprovantePagamento cobrar(Multa m, Usuario u) {
                return new ComprovantePagamento("X", m.getValor(), LocalDateTime.now(relogio), StatusPagamento.RECUSADO);
            }
            @Override public StatusPagamento consultarStatus(String id) { return StatusPagamento.RECUSADO; }
        };
        MultaService servico = new MultaService(ctx.multaRepositorio, ctx.emprestimoService, ctx.usuarioService,
                recusa, new MultaFixaDiaria(BigDecimal.ONE), relogio);
        assertFalse(servico.pagarMulta(multa.getId()).aprovado());
        assertEquals(StatusMulta.PENDENTE, ctx.multaService.buscar(multa.getId()).getStatus());
    }

    // -------------------------------------------------------------- renovação

    @Test
    void renovaAteOLimiteDeDuasVezes() {
        Emprestimo e = ctx.emprestimoFacade.emprestar(ana.getId(), exemplar.getId());
        Emprestimo r1 = ctx.emprestimoFacade.renovar(e.getId());
        assertEquals(hoje().plusDays(28), r1.getDataPrevistaDevolucao());
        assertEquals(1, r1.getRenovacoes());
        assertEquals(2, ctx.emprestimoFacade.renovar(e.getId()).getRenovacoes());
        assertThrows(NegocioException.class, () -> ctx.emprestimoFacade.renovar(e.getId()));
    }

    @Test
    void naoRenovaEmprestimoVencidoOuDevolvido() {
        Emprestimo e = ctx.emprestimoFacade.emprestar(ana.getId(), exemplar.getId());
        relogio.avancarDias(15);
        assertThrows(NegocioException.class, () -> ctx.emprestimoFacade.renovar(e.getId()));
        ctx.emprestimoFacade.devolver(e.getId());
        assertThrows(NegocioException.class, () -> ctx.emprestimoFacade.renovar(e.getId()));
    }

    @Test
    void naoRenovaQuandoHaFilaDeReservas() {
        Emprestimo e = ctx.emprestimoFacade.emprestar(ana.getId(), exemplar.getId());
        ctx.reservaService.criar(beto.getId(), livro.getId());
        assertThrows(NegocioException.class, () -> ctx.emprestimoFacade.renovar(e.getId()));
    }

    // ----------------------------------------------------------------- reservas

    @Test
    void soReservaQuandoNaoHaItemDisponivelENaoDuplica() {
        assertThrows(NegocioException.class, () -> ctx.reservaService.criar(beto.getId(), livro.getId()));
        ctx.emprestimoFacade.emprestar(ana.getId(), exemplar.getId());
        Reserva r = ctx.reservaService.criar(beto.getId(), livro.getId());
        assertEquals(StatusReserva.ATIVA, r.getStatus());
        assertThrows(ConflitoException.class, () -> ctx.reservaService.criar(beto.getId(), livro.getId()));
        assertThrows(NegocioException.class, () -> ctx.reservaService.criar(novoDoador("d@x.com", false).getId(), livro.getId()));
    }

    @Test
    void devolucaoSegurasItemParaOPrimeiroDaFilaENotificaEleApenas() {
        Emprestimo e = ctx.emprestimoFacade.emprestar(ana.getId(), exemplar.getId());
        UsuarioSite carlos = novoUsuarioSite("carlos@x.com");
        ctx.reservaService.criar(beto.getId(), livro.getId());
        relogio.avancarDias(1);
        ctx.reservaService.criar(carlos.getId(), livro.getId());

        ctx.emprestimoFacade.devolver(e.getId());

        assertEquals(StatusItem.RESERVADO, statusDoExemplar());
        assertEquals(1, ctx.notificacaoRepositorio.listarPorUsuario(beto.getId()).size());
        Notificacao n = ctx.notificacaoRepositorio.listarPorUsuario(beto.getId()).get(0);
        assertEquals(TipoEvento.LIVRO_DISPONIVEL, n.getTipo());
        assertTrue(n.getMensagem().contains("Dom Casmurro"));
        assertTrue(ctx.notificacaoRepositorio.listarPorUsuario(carlos.getId()).isEmpty());
        assertEquals(1, emailsEnviados.size());
        assertTrue(emailsEnviados.get(0).contains("beto@x.com"));

        assertThrows(NegocioException.class, () -> ctx.emprestimoFacade.emprestar(carlos.getId(), exemplar.getId()),
                "o item está segurado para o primeiro da fila");
        assertThrows(NegocioException.class, () -> ctx.emprestimoFacade.emprestar(ana.getId(), exemplar.getId()));

        ctx.emprestimoFacade.emprestar(beto.getId(), exemplar.getId());
        assertEquals(StatusItem.EMPRESTADO, statusDoExemplar());
        assertEquals(StatusReserva.ATENDIDA, ctx.reservaRepositorio.listarPorUsuario(beto.getId()).get(0).getStatus());
        assertEquals(StatusReserva.ATIVA, ctx.reservaRepositorio.listarPorUsuario(carlos.getId()).get(0).getStatus());
    }

    @Test
    void cancelarAUltimaReservaLiberaOItemSegurado() {
        Emprestimo e = ctx.emprestimoFacade.emprestar(ana.getId(), exemplar.getId());
        Reserva r = ctx.reservaService.criar(beto.getId(), livro.getId());
        ctx.emprestimoFacade.devolver(e.getId());
        assertEquals(StatusItem.RESERVADO, statusDoExemplar());
        ctx.reservaService.cancelar(r.getId());
        assertEquals(StatusItem.DISPONIVEL, statusDoExemplar());
        assertDoesNotThrow(() -> ctx.emprestimoFacade.emprestar(ana.getId(), exemplar.getId()));
    }

    @Test
    void reservaPodeSerAtendidaManualmenteEDepoisNaoCancela() {
        ctx.emprestimoFacade.emprestar(ana.getId(), exemplar.getId());
        Reserva r = ctx.reservaService.criar(beto.getId(), livro.getId());
        assertEquals(r.getId(), ctx.reservaService.proximaDaFila(livro.getId()).orElseThrow().getId());
        ctx.reservaService.atender(r.getId());
        assertTrue(ctx.reservaService.proximaDaFila(livro.getId()).isEmpty());
        assertThrows(NegocioException.class, () -> ctx.reservaService.cancelar(r.getId()));
    }

    // ------------------------------------------------------------------ atrasos

    @Test
    void sincronizaStatusAtrasadoEListaAtrasados() {
        Emprestimo e = ctx.emprestimoFacade.emprestar(ana.getId(), exemplar.getId());
        assertEquals(0, ctx.emprestimoService.atualizarStatusAtrasados());
        relogio.avancarDias(15);
        assertEquals(1, ctx.emprestimoService.listarAtrasados().size());
        assertEquals(1, ctx.emprestimoService.atualizarStatusAtrasados());
        assertEquals(StatusEmprestimo.ATRASADO, ctx.emprestimoService.buscar(e.getId()).getStatus());
        assertEquals(0, ctx.emprestimoService.atualizarStatusAtrasados(), "idempotente");
    }

    @Test
    void ajustaPrazoEmprestimoEmAbertoERemoveSoDevolvidos() {
        Emprestimo e = ctx.emprestimoFacade.emprestar(ana.getId(), exemplar.getId());
        Emprestimo ajustado = ctx.emprestimoService.ajustarPrazo(e.getId(), hoje().plusDays(30));
        assertEquals(hoje().plusDays(30), ajustado.getDataPrevistaDevolucao());
        assertThrows(NegocioException.class, () -> ctx.emprestimoService.remover(e.getId()));
        ctx.emprestimoFacade.devolver(e.getId());
        assertThrows(NegocioException.class, () -> ctx.emprestimoService.ajustarPrazo(e.getId(), hoje().plusDays(40)));
        ctx.emprestimoService.remover(e.getId());
        assertThrows(NaoEncontradoException.class, () -> ctx.emprestimoService.buscar(e.getId()));
    }

    @Test
    void listaEmprestimosPorUsuario() {
        ctx.emprestimoFacade.emprestar(ana.getId(), exemplar.getId());
        assertEquals(1, ctx.emprestimoService.listarPorUsuario(ana.getId()).size());
        assertTrue(ctx.emprestimoService.listarPorUsuario(beto.getId()).isEmpty());
        assertEquals(1, ctx.emprestimoService.listar().size());
    }
}
