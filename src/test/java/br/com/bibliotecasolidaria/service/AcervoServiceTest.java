package br.com.bibliotecasolidaria.service;

import br.com.bibliotecasolidaria.dto.DadosLivro;
import br.com.bibliotecasolidaria.dto.ItemConsultado;
import br.com.bibliotecasolidaria.exception.ConflitoException;
import br.com.bibliotecasolidaria.exception.NaoEncontradoException;
import br.com.bibliotecasolidaria.exception.NegocioException;
import br.com.bibliotecasolidaria.exception.ValidacaoException;
import br.com.bibliotecasolidaria.model.Ebook;
import br.com.bibliotecasolidaria.model.ExemplarFisico;
import br.com.bibliotecasolidaria.model.ItemAcervo;
import br.com.bibliotecasolidaria.model.Livro;
import br.com.bibliotecasolidaria.model.enums.CategoriaLivro;
import br.com.bibliotecasolidaria.model.enums.EstadoConservacao;
import br.com.bibliotecasolidaria.model.enums.StatusItem;
import br.com.bibliotecasolidaria.model.enums.TipoItemAcervo;
import br.com.bibliotecasolidaria.support.BancoTestBase;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AcervoServiceTest extends BancoTestBase {
    @Test
    void cadastraEBuscaLivros() {
        Livro l = novoLivro("Dom Casmurro", "111");
        assertNotNull(l.getId());
        assertEquals("Dom Casmurro", ctx.acervoService.buscarLivro(l.getId()).getTitulo());
        assertEquals(1, ctx.acervoService.buscarPorTitulo("casmurro").size());
        assertEquals(1, ctx.acervoService.buscarPorCategoria(CategoriaLivro.DIDATICO).size());
        assertTrue(ctx.acervoService.buscarPorIsbn("111").isPresent());
        assertThrows(NaoEncontradoException.class, () -> ctx.acervoService.buscarLivro(999));
    }

    @Test
    void validaDadosDoLivroEIsbnUnico() {
        novoLivro("A", "111");
        assertThrows(ConflitoException.class, () -> novoLivro("B", "111"));
        assertThrows(ValidacaoException.class, () -> ctx.acervoService.cadastrarLivro(new Livro(null, " ", "A", null, null, CategoriaLivro.OUTRO, 2000)));
        assertThrows(ValidacaoException.class, () -> ctx.acervoService.cadastrarLivro(new Livro(null, "T", "", null, null, CategoriaLivro.OUTRO, 2000)));
        assertThrows(ValidacaoException.class, () -> ctx.acervoService.cadastrarLivro(new Livro(null, "T", "A", null, null, null, 2000)));
        assertThrows(ValidacaoException.class, () -> ctx.acervoService.cadastrarLivro(new Livro(null, "T", "A", null, null, CategoriaLivro.OUTRO, 3000)));
        assertThrows(ValidacaoException.class, () -> ctx.acervoService.cadastrarLivro(new Livro(null, "T", "A", null, null, CategoriaLivro.OUTRO, -1)));
    }

    @Test
    void atualizaLivroSemConflitarComOPropriosIsbn() {
        Livro a = novoLivro("A", "111");
        Livro b = novoLivro("B", "222");
        a.setTitulo("A2");
        assertEquals("A2", ctx.acervoService.atualizarLivro(a).getTitulo());
        b.setIsbn("111");
        assertThrows(ConflitoException.class, () -> ctx.acervoService.atualizarLivro(b));
        assertThrows(ValidacaoException.class, () -> ctx.acervoService.atualizarLivro(new Livro(null, "X", "Y", null, null, CategoriaLivro.OUTRO, 2000)));
    }

    @Test
    void removeLivroSemItensMasNaoOReferenciado() {
        Livro sem = novoLivro("A", "1");
        ctx.acervoService.removerLivro(sem.getId());
        assertThrows(NaoEncontradoException.class, () -> ctx.acervoService.buscarLivro(sem.getId()));
        Livro com = novoLivro("B", "2");
        novoExemplar(com.getId(), "T1");
        assertThrows(ConflitoException.class, () -> ctx.acervoService.removerLivro(com.getId()));
    }

    @Test
    void cadastraItensValidandoCampos() {
        Livro l = novoLivro("A", "1");
        novoExemplar(l.getId(), "T1");
        assertThrows(ConflitoException.class, () -> novoExemplar(l.getId(), "T1"), "tombo repetido");
        assertThrows(ValidacaoException.class, () -> ctx.acervoService.cadastrarItem(
                new ExemplarFisico(null, l.getId(), StatusItem.DISPONIVEL, " ", EstadoConservacao.BOM, null)));
        assertThrows(ValidacaoException.class, () -> ctx.acervoService.cadastrarItem(
                new ExemplarFisico(null, l.getId(), StatusItem.DISPONIVEL, "T8", EstadoConservacao.BOM, null)), "localização");
        assertThrows(NaoEncontradoException.class, () -> novoExemplar(999L, "T9"));
        assertThrows(ValidacaoException.class, () -> ctx.acervoService.cadastrarItem(new Ebook(null, l.getId(), StatusItem.DISPONIVEL, "", "PDF")));
        Ebook e = (Ebook) ctx.acervoService.cadastrarItem(new Ebook(null, l.getId(), null, "http://x/a.pdf", "PDF"));
        assertEquals(StatusItem.DISPONIVEL, e.getStatus());
    }

    @Test
    void consultaDisponiveisComFiltros() {
        Livro dom = novoLivro("Dom Casmurro", "1");
        Livro peter = ctx.acervoService.cadastrarLivro(new Livro(null, "Peter Pan", "Barrie", null, "2", CategoriaLivro.INFANTIL, 1911));
        ExemplarFisico e1 = novoExemplar(dom.getId(), "T1");
        novoExemplar(peter.getId(), "T2");
        ctx.acervoService.cadastrarItem(new Ebook(null, dom.getId(), StatusItem.DISPONIVEL, "http://x", "PDF"));
        ctx.acervoService.marcarEmprestado(e1.getId());

        assertEquals(2, ctx.acervoService.consultarDisponiveis(null, null, null).size());
        List<ItemConsultado> porTitulo = ctx.acervoService.consultarDisponiveis("casmurro", null, null);
        assertEquals(1, porTitulo.size());
        assertEquals(TipoItemAcervo.EBOOK, porTitulo.get(0).item().getTipo());
        assertEquals(1, ctx.acervoService.consultarDisponiveis(null, CategoriaLivro.INFANTIL, null).size());
        assertEquals(1, ctx.acervoService.consultarDisponiveis(null, null, TipoItemAcervo.EXEMPLAR_FISICO).size());
        assertTrue(ctx.acervoService.consultarDisponiveis("inexistente", null, null).isEmpty());
    }

    @Test
    void marcacoesDeStatusEProtecaoNaRemocao() {
        Livro l = novoLivro("A", "1");
        ExemplarFisico e = novoExemplar(l.getId(), "T1");
        assertEquals(StatusItem.EMPRESTADO, ctx.acervoService.marcarEmprestado(e.getId()).getStatus());
        assertThrows(NegocioException.class, () -> ctx.acervoService.marcarEmprestado(e.getId()));
        assertThrows(NegocioException.class, () -> ctx.acervoService.removerItem(e.getId()));
        assertEquals(StatusItem.DISPONIVEL, ctx.acervoService.marcarDisponivel(e.getId()).getStatus());
        assertEquals(StatusItem.RESERVADO, ctx.acervoService.marcarReservado(e.getId()).getStatus());
        ctx.acervoService.marcarDisponivel(e.getId());
        ctx.acervoService.removerItem(e.getId());
        assertThrows(NaoEncontradoException.class, () -> ctx.acervoService.buscarItem(e.getId()));
    }

    @Test
    void atualizaItemMasNaoMudaSeuTipo() {
        Livro l = novoLivro("A", "1");
        ExemplarFisico e = novoExemplar(l.getId(), "T1");
        e.setLocalizacao("Estante 9");
        ExemplarFisico salvo = (ExemplarFisico) ctx.acervoService.atualizarItem(e);
        assertEquals("Estante 9", salvo.getLocalizacao());
        ItemAcervo trocado = new Ebook(e.getId(), l.getId(), StatusItem.DISPONIVEL, "http://x", "PDF");
        assertThrows(ValidacaoException.class, () -> ctx.acervoService.atualizarItem(trocado));
    }

    @Test
    void preencheCadastroPeloAcervoGlobal() {
        DadosLivro d = ctx.acervoService.preencherCadastro("9780000000028");
        assertEquals("O Alienista", d.titulo());
        assertThrows(NaoEncontradoException.class, () -> ctx.acervoService.preencherCadastro("0"));
    }
}
