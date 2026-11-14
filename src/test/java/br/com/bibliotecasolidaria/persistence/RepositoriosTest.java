package br.com.bibliotecasolidaria.persistence;

import br.com.bibliotecasolidaria.exception.ConflitoException;
import br.com.bibliotecasolidaria.exception.NaoEncontradoException;
import br.com.bibliotecasolidaria.model.*;
import br.com.bibliotecasolidaria.model.enums.*;
import br.com.bibliotecasolidaria.support.BancoTestBase;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RepositoriosTest extends BancoTestBase {
    @Test
    void usuarioCrudParaOsTresTipos() {
        UsuarioRepositorio r = ctx.usuarioRepositorio;
        Usuario d = r.salvar(new Doador(null, "Ana", "ana@x.com", "h", true, "123", true));
        Usuario s = r.salvar(new UsuarioSite(null, "Beto", "beto@x.com", "h", true, "6299", "Rua 1"));
        Usuario f = r.salvar(new Funcionario(null, "Carla", "carla@x.com", "h", true, "M1", "Bibliotecária"));
        assertNotNull(d.getId());

        Doador lido = (Doador) r.buscarPorId(d.getId()).orElseThrow();
        assertEquals("123", lido.getDocumento());
        assertTrue(lido.isPessoaJuridica());
        assertEquals("Rua 1", ((UsuarioSite) r.buscarPorId(s.getId()).orElseThrow()).getEndereco());
        assertEquals("M1", ((Funcionario) r.buscarPorId(f.getId()).orElseThrow()).getMatricula());
        assertEquals(3, r.listarTodos().size());
        assertEquals(1, r.listarPorTipo(TipoUsuario.FUNCIONARIO).size());

        lido.setNome("Ana Maria");
        r.salvar(lido);
        assertEquals("Ana Maria", r.buscarPorId(d.getId()).orElseThrow().getNome());

        r.remover(d.getId());
        assertTrue(r.buscarPorId(d.getId()).isEmpty());
    }

    @Test
    void buscaPorEmailIgnoraCaixaEEmailDuplicadoEhConflito() {
        UsuarioRepositorio r = ctx.usuarioRepositorio;
        r.salvar(new Doador(null, "Ana", "ana@x.com", "h", true, "1", false));
        assertTrue(r.buscarPorEmail("ANA@X.COM").isPresent());
        assertTrue(r.buscarPorEmail("outro@x.com").isEmpty());
        assertThrows(ConflitoException.class,
                () -> r.salvar(new Doador(null, "Outra", "ana@x.com", "h", true, "2", false)));
    }

    @Test
    void atualizarRegistroInexistenteLancaNaoEncontrado() {
        assertThrows(NaoEncontradoException.class, () -> ctx.livroRepositorio.salvar(
                new Livro(999L, "T", "A", null, null, CategoriaLivro.OUTRO, 2000)));
    }

    @Test
    void livroEItensComConsultasEspecificas() {
        Livro l = ctx.livroRepositorio.salvar(new Livro(null, "Dom Casmurro", "Machado", "Ed", "111", CategoriaLivro.DIDATICO, 1899));
        ctx.livroRepositorio.salvar(new Livro(null, "Peter Pan", "Barrie", null, null, CategoriaLivro.INFANTIL, 1911));
        assertEquals(1, ctx.livroRepositorio.buscarPorTitulo("casmur").size());
        assertEquals(1, ctx.livroRepositorio.buscarPorCategoria(CategoriaLivro.INFANTIL).size());
        assertEquals(l.getId(), ctx.livroRepositorio.buscarPorIsbn("111").orElseThrow().getId());

        ItemAcervoRepositorio itens = ctx.itemAcervoRepositorio;
        ItemAcervo ex = itens.salvar(new ExemplarFisico(null, l.getId(), StatusItem.DISPONIVEL, "T1", EstadoConservacao.BOM, "E1"));
        ItemAcervo ocupado = itens.salvar(new ExemplarFisico(null, l.getId(), StatusItem.EMPRESTADO, "T2", EstadoConservacao.OTIMO, "E2"));
        ItemAcervo eb = itens.salvar(new Ebook(null, l.getId(), StatusItem.EMPRESTADO, "http://x/a.pdf", "PDF"));
        assertEquals(3, itens.listarPorLivro(l.getId()).size());
        List<Long> disponiveis = itens.listarDisponiveis().stream().map(ItemAcervo::getId).toList();
        assertTrue(disponiveis.contains(ex.getId()));
        assertTrue(disponiveis.contains(eb.getId()), "e-book conta como disponível");
        assertFalse(disponiveis.contains(ocupado.getId()));
        assertEquals("T2", ((ExemplarFisico) itens.buscarPorCodigoTombo("T2").orElseThrow()).getCodigoTombo());
        assertTrue(itens.buscarPorId(eb.getId()).orElseThrow() instanceof Ebook);
    }

    @Test
    void naoRemoveLivroReferenciadoPorItens() {
        Livro l = novoLivro("T", "1");
        novoExemplar(l.getId(), "T1");
        assertThrows(ConflitoException.class, () -> ctx.livroRepositorio.remover(l.getId()));
    }

    @Test
    void doacoesDeTodosOsTiposEConsultas() {
        Doador d = novoDoador("d@x.com", false);
        DoacaoRepositorio r = ctx.doacaoRepositorio;
        Doacao a = r.salvar(new DoacaoLivroFisico(null, d.getId(), hoje(), StatusDoacao.ACEITA, "T", "A", "111", CategoriaLivro.OUTRO, EstadoConservacao.BOM, 3));
        r.salvar(new DoacaoEbook(null, d.getId(), hoje(), StatusDoacao.REGISTRADA, "T", "A", "http://x", "PDF"));
        r.salvar(new DoacaoDinheiro(null, d.getId(), hoje().minusDays(30), StatusDoacao.ACEITA, new BigDecimal("10.50"), "PIX", "c1"));
        assertEquals(3, r.listarPorDoador(d.getId()).size());
        assertEquals(2, r.listarPorStatus(StatusDoacao.ACEITA).size());
        assertEquals(2, r.listarPorPeriodo(hoje().minusDays(1), hoje()).size());
        DoacaoLivroFisico lida = (DoacaoLivroFisico) r.buscarPorId(a.getId()).orElseThrow();
        assertEquals(3, lida.getQuantidade());
        assertEquals(EstadoConservacao.BOM, lida.getEstado());
        DoacaoDinheiro din = (DoacaoDinheiro) r.listarPorStatus(StatusDoacao.ACEITA).stream().filter(x -> x instanceof DoacaoDinheiro).findFirst().orElseThrow();
        assertEquals(new BigDecimal("10.50"), din.getValor());
    }

    @Test
    void emprestimosMultasReservasBonificacoesENotificacoes() {
        UsuarioSite u = novoUsuarioSite("u@x.com");
        Livro l = novoLivro("T", "1");
        ExemplarFisico ex = novoExemplar(l.getId(), "T1");

        EmprestimoRepositorio er = ctx.emprestimoRepositorio;
        Emprestimo e = er.salvar(new Emprestimo(null, u.getId(), ex.getId(), hoje().minusDays(20), hoje().minusDays(6), null, 0, StatusEmprestimo.ATIVO));
        er.salvar(new Emprestimo(null, u.getId(), ex.getId(), hoje(), hoje().plusDays(14), null, 0, StatusEmprestimo.ATIVO));
        assertEquals(1, er.listarAtrasados(hoje()).size());
        assertEquals(2, er.listarPorUsuario(u.getId()).size());
        assertEquals(2, er.listarEmAbertoPorItem(ex.getId()).size());
        assertEquals(1, er.listarEmAbertoComVencimentoEntre(hoje(), hoje().plusDays(14)).size());
        assertEquals(1, er.listarPorPeriodo(hoje().minusDays(1), hoje()).size());

        MultaRepositorio mr = ctx.multaRepositorio;
        Multa m = mr.salvar(new Multa(null, e.getId(), new BigDecimal("6.00"), hoje(), null, StatusMulta.PENDENTE));
        assertEquals(1, mr.listarPendentes(u.getId()).size());
        assertEquals(m.getId(), mr.buscarPorEmprestimo(e.getId()).orElseThrow().getId());
        assertEquals(1, mr.listarGeradasNoPeriodo(hoje(), hoje()).size());
        assertThrows(ConflitoException.class, () -> mr.salvar(new Multa(null, e.getId(), BigDecimal.ONE, hoje(), null, StatusMulta.PENDENTE)),
                "só uma multa por empréstimo");

        ReservaRepositorio rr = ctx.reservaRepositorio;
        UsuarioSite u2 = novoUsuarioSite("u2@x.com");
        rr.salvar(new Reserva(null, u2.getId(), l.getId(), hoje(), StatusReserva.ATIVA));
        Reserva primeira = rr.salvar(new Reserva(null, u.getId(), l.getId(), hoje().minusDays(1), StatusReserva.ATIVA));
        assertEquals(primeira.getId(), rr.proximaDaFila(l.getId()).orElseThrow().getId(), "fila por data");
        assertEquals(2, rr.listarAtivasPorLivro(l.getId()).size());
        assertTrue(rr.buscarAtiva(u2.getId(), l.getId()).isPresent());
        assertEquals(1, rr.listarPorUsuario(u.getId()).size());

        Doador d = novoDoador("d@x.com", false);
        BonificacaoRepositorio br = ctx.bonificacaoRepositorio;
        br.salvar(new Bonificacao(null, d.getId(), TipoBonificacao.SELO, "Selo", hoje()));
        assertEquals(1, br.listarPorDoador(d.getId()).size());

        NotificacaoRepositorio nr = ctx.notificacaoRepositorio;
        LocalDateTime agora = LocalDateTime.of(2026, 11, 10, 9, 0);
        Notificacao n = nr.salvar(new Notificacao(null, u.getId(), TipoEvento.LIVRO_DISPONIVEL, "oi", agora, false));
        nr.salvar(new Notificacao(null, u.getId(), TipoEvento.LIVRO_DISPONIVEL, "lida", agora, true));
        assertEquals(2, nr.listarPorUsuario(u.getId()).size());
        assertEquals(1, nr.listarNaoLidas(u.getId()).size());
        assertTrue(nr.existeNoDia(u.getId(), TipoEvento.LIVRO_DISPONIVEL, "oi", agora.toLocalDate()));
        assertFalse(nr.existeNoDia(u.getId(), TipoEvento.LIVRO_DISPONIVEL, "oi", agora.toLocalDate().plusDays(1)));
        assertFalse(nr.existeNoDia(u.getId(), TipoEvento.DOACAO_ACEITA, "oi", agora.toLocalDate()));
        assertNotNull(n.getId());
    }

    @Test
    void transacaoDesfazTudoQuandoHaExcecao() {
        assertThrows(IllegalStateException.class, () -> conexao.emTransacao(() -> {
            novoLivro("Será desfeito", "999");
            throw new IllegalStateException("falha");
        }));
        assertTrue(ctx.livroRepositorio.buscarPorIsbn("999").isEmpty());
    }

    @Test
    void transacaoConfirmaEAninhadaParticipaDaExterna() {
        conexao.emTransacao(() -> {
            novoLivro("A", "1");
            conexao.emTransacao(() -> novoLivro("B", "2"));
        });
        assertEquals(2, ctx.livroRepositorio.listarTodos().size());

        assertThrows(RuntimeException.class, () -> conexao.emTransacao(() -> {
            conexao.emTransacao(() -> novoLivro("C", "3"));
            throw new RuntimeException("desfaz também a interna");
        }));
        assertEquals(2, ctx.livroRepositorio.listarTodos().size());
    }
}
