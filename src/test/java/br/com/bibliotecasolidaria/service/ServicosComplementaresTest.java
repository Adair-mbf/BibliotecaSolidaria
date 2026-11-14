package br.com.bibliotecasolidaria.service;

import br.com.bibliotecasolidaria.dto.DoacaoDTO;
import br.com.bibliotecasolidaria.dto.RelatorioDoacoes;
import br.com.bibliotecasolidaria.dto.RelatorioEmprestimos;
import br.com.bibliotecasolidaria.exception.ConflitoException;
import br.com.bibliotecasolidaria.exception.NaoEncontradoException;
import br.com.bibliotecasolidaria.exception.NegocioException;
import br.com.bibliotecasolidaria.exception.ValidacaoException;
import br.com.bibliotecasolidaria.model.*;
import br.com.bibliotecasolidaria.model.enums.*;
import br.com.bibliotecasolidaria.support.BancoTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ServicosComplementaresTest extends BancoTestBase {
    private UsuarioSite ana;
    private Doador doador;
    private ExemplarFisico exemplar;
    private Livro livro;

    @BeforeEach
    void cenario() {
        ana = novoUsuarioSite("ana@x.com");
        doador = novoDoador("d@x.com", false);
        livro = novoLivro("Dom Casmurro", "111");
        exemplar = novoExemplar(livro.getId(), "T1");
    }

    private Emprestimo emprestimoAtrasado(int diasAposPrazo) {
        Emprestimo e = ctx.emprestimoFacade.emprestar(ana.getId(), exemplar.getId());
        relogio.avancarDias(14 + diasAposPrazo);
        return e;
    }

    // ------------------------------------------------------------------ multas

    @Test
    void geraMultaIdempotenteEExigeAtraso() {
        Emprestimo e = ctx.emprestimoFacade.emprestar(ana.getId(), exemplar.getId());
        assertThrows(NegocioException.class, () -> ctx.multaService.gerarMulta(e), "sem atraso");
        relogio.avancarDias(17);
        Multa m1 = ctx.multaService.gerarMulta(e);
        Multa m2 = ctx.multaService.gerarMulta(e);
        assertEquals(m1.getId(), m2.getId());
        assertEquals(new BigDecimal("3.00"), m1.getValor());
        assertEquals(1, ctx.multaService.listar().size());
    }

    @Test
    void crudManualDeMultas() {
        Emprestimo e = ctx.emprestimoFacade.emprestar(ana.getId(), exemplar.getId());
        Multa m = ctx.multaService.criar(e.getId(), new BigDecimal("12.345"));
        assertEquals(new BigDecimal("12.35"), m.getValor());
        assertThrows(ConflitoException.class, () -> ctx.multaService.criar(e.getId(), BigDecimal.ONE));
        assertThrows(ValidacaoException.class, () -> ctx.multaService.criar(e.getId(), BigDecimal.ZERO));
        assertThrows(NaoEncontradoException.class, () -> ctx.multaService.criar(999L, BigDecimal.ONE));

        assertEquals(new BigDecimal("5.00"), ctx.multaService.atualizarValor(m.getId(), new BigDecimal("5")).getValor());
        assertEquals(1, ctx.multaService.listarPendentes(ana.getId()).size());

        ctx.emprestimoFacade.pagarMulta(m.getId());
        assertThrows(NegocioException.class, () -> ctx.multaService.atualizarValor(m.getId(), BigDecimal.TEN));
        assertThrows(NegocioException.class, () -> ctx.multaService.remover(m.getId()));
    }

    @Test
    void perdoaMultaPendente() {
        Emprestimo e = ctx.emprestimoFacade.emprestar(ana.getId(), exemplar.getId());
        Multa m = ctx.multaService.criar(e.getId(), BigDecimal.TEN);
        ctx.multaService.remover(m.getId());
        assertThrows(NaoEncontradoException.class, () -> ctx.multaService.buscar(m.getId()));
    }

    // -------------------------------------------------------------- bonificação

    @Test
    void crudManualDeBonificacoes() {
        Bonificacao b = ctx.bonificacaoService.conceder(doador.getId(), TipoBonificacao.PREMIO, "Doador do ano");
        assertEquals(hoje(), b.getDataConcessao());
        assertEquals("Doador do ano", ctx.bonificacaoService.buscar(b.getId()).getDescricao());
        Bonificacao up = ctx.bonificacaoService.atualizar(b.getId(), TipoBonificacao.SELO, " ");
        assertEquals(TipoBonificacao.SELO, up.getTipo());
        assertEquals("Doador do ano", up.getDescricao(), "descrição em branco é mantida");
        assertEquals(1, ctx.bonificacaoService.listar().size());
        ctx.bonificacaoService.remover(b.getId());
        assertThrows(NaoEncontradoException.class, () -> ctx.bonificacaoService.buscar(b.getId()));
    }

    @Test
    void concessaoManualValidaDoadorETexto() {
        assertThrows(NegocioException.class, () -> ctx.bonificacaoService.conceder(ana.getId(), TipoBonificacao.SELO, "x"));
        assertThrows(ValidacaoException.class, () -> ctx.bonificacaoService.conceder(doador.getId(), null, "x"));
        assertThrows(ValidacaoException.class, () -> ctx.bonificacaoService.conceder(doador.getId(), TipoBonificacao.SELO, " "));
    }

    // -------------------------------------------------------------- notificação

    @Test
    void crudManualDeNotificacoes() {
        Notificacao n = ctx.notificacaoService.criar(ana.getId(), TipoEvento.LIVRO_DISPONIVEL, "Olá");
        assertFalse(n.isLida());
        assertEquals(1, ctx.notificacaoService.listarNaoLidas(ana.getId()).size());
        assertTrue(ctx.notificacaoService.marcarComoLida(n.getId()).isLida());
        assertTrue(ctx.notificacaoService.listarNaoLidas(ana.getId()).isEmpty());
        assertEquals(1, ctx.notificacaoService.listarPorUsuario(ana.getId()).size());
        assertEquals("Nova", ctx.notificacaoService.atualizarMensagem(n.getId(), " Nova ").getMensagem());
        assertThrows(ValidacaoException.class, () -> ctx.notificacaoService.atualizarMensagem(n.getId(), ""));
        assertThrows(NaoEncontradoException.class, () -> ctx.notificacaoService.criar(999L, TipoEvento.DOACAO_ACEITA, "x"));
        ctx.notificacaoService.remover(n.getId());
        assertTrue(ctx.notificacaoService.listar().isEmpty());
    }

    // ------------------------------------------------------------------ agendador

    @Test
    void avisaEmprestimosPertoDoVencimentoUmaVezPorDia() {
        ctx.emprestimoFacade.emprestar(ana.getId(), exemplar.getId());
        assertEquals(0, ctx.agendadorVencimentos.verificarVencimentosProximos(), "faltam 14 dias");
        relogio.avancarDias(12); // faltam 2
        assertEquals(1, ctx.agendadorVencimentos.verificarVencimentosProximos());
        assertEquals(0, ctx.agendadorVencimentos.verificarVencimentosProximos(), "sem repetir no mesmo dia");
        Notificacao n = ctx.notificacaoRepositorio.listarPorUsuario(ana.getId()).get(0);
        assertEquals(TipoEvento.VENCIMENTO_PROXIMO, n.getTipo());
        assertTrue(n.getMensagem().contains("Dom Casmurro"));
        relogio.avancarDias(1);
        assertEquals(1, ctx.agendadorVencimentos.verificarVencimentosProximos(), "novo dia, novo aviso");
    }

    @Test
    void agendadorTambemMarcaOsEmprestimosAtrasados() {
        Emprestimo e = emprestimoAtrasado(2);
        ctx.agendadorVencimentos.verificarVencimentosProximos();
        assertEquals(StatusEmprestimo.ATRASADO, ctx.emprestimoService.buscar(e.getId()).getStatus());
    }

    // ---------------------------------------------------------------- relatórios

    @Test
    void relatorioDeEmprestimosContaStatusEMultas() {
        Emprestimo e = ctx.emprestimoFacade.emprestar(ana.getId(), exemplar.getId());
        relogio.avancarDias(20);
        ctx.emprestimoFacade.devolver(e.getId()); // 6 dias de atraso => R$ 6,00
        Livro outro = novoLivro("Outro", "222");
        ExemplarFisico ex2 = novoExemplar(outro.getId(), "T2");
        UsuarioSite beto = novoUsuarioSite("beto@x.com");
        ctx.emprestimoFacade.emprestar(beto.getId(), ex2.getId());
        relogio.avancarDias(30);

        RelatorioEmprestimos r = ctx.relatorioService.emprestimosPorPeriodo(hoje().minusDays(60), hoje());
        assertEquals(2, r.total());
        assertEquals(1, r.devolvidos());
        assertEquals(1, r.ativos());
        assertEquals(1, r.atrasados());
        assertEquals(1, r.multasGeradas());
        assertEquals(new BigDecimal("6.00"), r.valorMultas());
        assertTrue(r.toString().contains("Total: 2"));
        assertEquals(0, ctx.relatorioService.emprestimosPorPeriodo(hoje().plusDays(1), hoje().plusDays(2)).total());
    }

    @Test
    void relatorioDeDoacoesSomaApenasAceitas() {
        ctx.doacaoFacade.registrarDoacao(new DoacaoDTO(doador.getId(), TipoDoacao.LIVRO_FISICO,
                Map.of("titulo", "T", "autor", "A", "isbn", "9", "categoria", "OUTRO", "estado", "BOM", "quantidade", 4)));
        ctx.doacaoFacade.registrarDoacao(new DoacaoDTO(doador.getId(), TipoDoacao.LIVRO_FISICO,
                Map.of("titulo", "T", "autor", "A", "isbn", "8", "categoria", "OUTRO", "estado", "RUIM", "quantidade", 9)));
        ctx.doacaoFacade.registrarDoacao(new DoacaoDTO(doador.getId(), TipoDoacao.DINHEIRO, Map.of("valor", "30.50", "meioPagamento", "PIX")));

        RelatorioDoacoes r = ctx.relatorioService.doacoesPorPeriodo(hoje(), hoje());
        assertEquals(3, r.total());
        assertEquals(4, r.livrosFisicosRecebidos());
        assertEquals(new BigDecimal("30.50"), r.totalArrecadado());
        assertEquals(2, r.porTipo().get(TipoDoacao.LIVRO_FISICO));
        assertEquals(2, r.porStatus().get(StatusDoacao.ACEITA));
        assertEquals(1, r.porStatus().get(StatusDoacao.RECUSADA));
        assertTrue(r.toString().contains("arrecadado"));
    }

    @Test
    void relatorioExigePeriodoValido() {
        assertThrows(ValidacaoException.class, () -> ctx.relatorioService.emprestimosPorPeriodo(hoje(), hoje().minusDays(1)));
        assertThrows(ValidacaoException.class, () -> ctx.relatorioService.doacoesPorPeriodo(null, hoje()));
    }
}
