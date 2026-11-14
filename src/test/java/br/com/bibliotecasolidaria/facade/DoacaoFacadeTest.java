package br.com.bibliotecasolidaria.facade;

import br.com.bibliotecasolidaria.dto.DoacaoDTO;
import br.com.bibliotecasolidaria.exception.NaoEncontradoException;
import br.com.bibliotecasolidaria.exception.NegocioException;
import br.com.bibliotecasolidaria.exception.ValidacaoException;
import br.com.bibliotecasolidaria.model.*;
import br.com.bibliotecasolidaria.model.enums.*;
import br.com.bibliotecasolidaria.support.BancoTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DoacaoFacadeTest extends BancoTestBase {
    private Doador pessoa;

    @BeforeEach
    void cenario() {
        pessoa = novoDoador("pessoa@x.com", false);
    }

    private Map<String, Object> livros(String isbn, String estado, int qtd) {
        return new HashMap<>(Map.of("titulo", "Dom Casmurro", "autor", "Machado de Assis", "isbn", isbn,
                "categoria", "DIDATICO", "estado", estado, "quantidade", qtd));
    }

    private Doacao doarLivros(Doador d, String isbn, String estado, int qtd) {
        return ctx.doacaoFacade.registrarDoacao(new DoacaoDTO(d.getId(), TipoDoacao.LIVRO_FISICO, livros(isbn, estado, qtd)));
    }

    private Doacao doarDinheiro(Doador d, String valor) {
        return ctx.doacaoFacade.registrarDoacao(new DoacaoDTO(d.getId(), TipoDoacao.DINHEIRO,
                Map.of("valor", valor, "meioPagamento", "PIX")));
    }

    @Test
    void livrosAceitosViramExemplaresDisponiveisENotificamODoador() {
        Doacao d = doarLivros(pessoa, "9780000000011", "BOM", 3);
        assertEquals(StatusDoacao.ACEITA, d.getStatus());

        Livro livro = ctx.acervoService.buscarPorIsbn("9780000000011").orElseThrow();
        assertEquals("Editora Simulada", livro.getEditora(), "preenchido pelo acervo global");
        assertEquals(1899, livro.getAnoPublicacao());
        List<ItemAcervo> itens = ctx.acervoService.listarItensPorLivro(livro.getId());
        assertEquals(3, itens.size());
        assertTrue(itens.stream().allMatch(i -> i.getStatus() == StatusItem.DISPONIVEL && i instanceof ExemplarFisico));
        assertEquals(3, itens.stream().map(i -> ((ExemplarFisico) i).getCodigoTombo()).distinct().count(), "tombos únicos");

        List<Notificacao> avisos = ctx.notificacaoRepositorio.listarPorUsuario(pessoa.getId());
        assertEquals(1, avisos.size());
        assertEquals(TipoEvento.DOACAO_ACEITA, avisos.get(0).getTipo());
        assertEquals(1, emailsEnviados.size());
        assertTrue(ctx.bonificacaoService.listarPorDoador(pessoa.getId()).isEmpty());
    }

    @Test
    void novaDoacaoDoMesmoIsbnReaproveitaOLivro() {
        doarLivros(pessoa, "555", "OTIMO", 2);
        doarLivros(pessoa, "555", "BOM", 2);
        assertEquals(1, ctx.acervoService.listarLivros().size());
        assertEquals(4, ctx.acervoService.listarItens().size());
        assertEquals(4, ctx.acervoService.listarItens().stream().map(i -> ((ExemplarFisico) i).getCodigoTombo()).distinct().count());
    }

    @Test
    void livroEmMauEstadoEhRecusadoESemEfeitosColaterais() {
        Doacao d = doarLivros(pessoa, "555", "RUIM", 5);
        assertEquals(StatusDoacao.RECUSADA, d.getStatus());
        assertTrue(ctx.acervoService.listarItens().isEmpty());
        assertTrue(ctx.acervoService.listarLivros().isEmpty());
        assertTrue(ctx.notificacaoRepositorio.listarPorUsuario(pessoa.getId()).isEmpty());
    }

    @Test
    void dezLivrosGeramCertificadoCitadoNaNotificacao() {
        doarLivros(pessoa, "555", "BOM", 10);
        List<Bonificacao> b = ctx.bonificacaoService.listarPorDoador(pessoa.getId());
        assertEquals(1, b.size());
        assertEquals(TipoBonificacao.CERTIFICADO, b.get(0).getTipo());
        assertTrue(ctx.notificacaoRepositorio.listarPorUsuario(pessoa.getId()).get(0).getMensagem().contains(b.get(0).getDescricao()));
    }

    @Test
    void empresaComCincoLivrosGanhaParceriaMasPessoaFisicaNao() {
        Doador empresa = novoDoador("acme@x.com", true);
        doarLivros(empresa, "1", "BOM", 5);
        doarLivros(pessoa, "2", "BOM", 5);
        assertEquals(List.of(TipoBonificacao.PARCERIA), ctx.bonificacaoService.listarPorDoador(empresa.getId()).stream().map(Bonificacao::getTipo).toList());
        assertTrue(ctx.bonificacaoService.listarPorDoador(pessoa.getId()).isEmpty());
    }

    @Test
    void ebookAceitoViraItemDigitalDisponivel() {
        Doacao d = ctx.doacaoFacade.registrarDoacao(new DoacaoDTO(pessoa.getId(), TipoDoacao.EBOOK,
                Map.of("titulo", "Iracema", "autor", "José de Alencar", "arquivoUrl", "http://x/i.epub", "formato", "epub")));
        assertEquals(StatusDoacao.ACEITA, d.getStatus());
        ItemAcervo item = ctx.acervoService.listarItens().get(0);
        assertTrue(item instanceof Ebook b && b.getFormato().equals("EPUB"));
        assertEquals("Iracema", ctx.acervoService.buscarLivro(item.getLivroId()).getTitulo());
    }

    @Test
    void ebookEmFormatoNaoSuportadoEhRecusado() {
        Doacao d = ctx.doacaoFacade.registrarDoacao(new DoacaoDTO(pessoa.getId(), TipoDoacao.EBOOK,
                Map.of("titulo", "X", "autor", "Y", "arquivoUrl", "http://x/i.doc", "formato", "DOC")));
        assertEquals(StatusDoacao.RECUSADA, d.getStatus());
        assertTrue(ctx.acervoService.listarItens().isEmpty());
    }

    @Test
    void doacaoEmDinheiroConcedeSeloAPartirDeCem() {
        assertEquals(StatusDoacao.ACEITA, doarDinheiro(pessoa, "99.99").getStatus());
        assertTrue(ctx.bonificacaoService.listarPorDoador(pessoa.getId()).isEmpty());
        doarDinheiro(pessoa, "100");
        assertEquals(TipoBonificacao.SELO, ctx.bonificacaoService.listarPorDoador(pessoa.getId()).get(0).getTipo());
        assertTrue(ctx.acervoService.listarItens().isEmpty(), "dinheiro não gera itens");
    }

    @Test
    void dadosObrigatoriosAusentesSaoRejeitadosSemRegistrar() {
        assertThrows(ValidacaoException.class, () -> ctx.doacaoFacade.registrarDoacao(new DoacaoDTO(pessoa.getId(), TipoDoacao.DINHEIRO, Map.of("valor", "0", "meioPagamento", "PIX"))));
        assertThrows(ValidacaoException.class, () -> ctx.doacaoFacade.registrarDoacao(new DoacaoDTO(pessoa.getId(), TipoDoacao.DINHEIRO, Map.of("valor", "abc", "meioPagamento", "PIX"))));
        assertThrows(ValidacaoException.class, () -> ctx.doacaoFacade.registrarDoacao(new DoacaoDTO(pessoa.getId(), TipoDoacao.LIVRO_FISICO, livros("1", "BOM", 0))));
        assertThrows(ValidacaoException.class, () -> ctx.doacaoFacade.registrarDoacao(new DoacaoDTO(pessoa.getId(), TipoDoacao.LIVRO_FISICO, livros("1", "EXCELENTE", 1))));
        assertThrows(ValidacaoException.class, () -> ctx.doacaoFacade.registrarDoacao(new DoacaoDTO(pessoa.getId(), TipoDoacao.EBOOK, Map.of("titulo", "X"))));
        assertThrows(ValidacaoException.class, () -> ctx.doacaoFacade.registrarDoacao(new DoacaoDTO(null, TipoDoacao.EBOOK, Map.of())));
        assertTrue(ctx.doacaoService.listar().isEmpty(), "nada deve ficar registrado");
    }

    @Test
    void doadorPrecisaExistirSerDoadorEEstarAtivo() {
        assertThrows(NaoEncontradoException.class, () -> ctx.doacaoFacade.registrarDoacao(new DoacaoDTO(999L, TipoDoacao.DINHEIRO, Map.of("valor", "5", "meioPagamento", "PIX"))));
        UsuarioSite site = novoUsuarioSite("s@x.com");
        assertThrows(NegocioException.class, () -> ctx.doacaoFacade.registrarDoacao(new DoacaoDTO(site.getId(), TipoDoacao.DINHEIRO, Map.of("valor", "5", "meioPagamento", "PIX"))));
        ctx.usuarioService.definirAtivo(pessoa.getId(), false);
        assertThrows(NegocioException.class, () -> doarDinheiro(pessoa, "5"));
    }

    @Test
    void fluxoManualDeAceiteERecusaParaDoacoesPendentes() {
        Doacao pendente = ctx.doacaoService.registrar(new DoacaoDTO(pessoa.getId(), TipoDoacao.LIVRO_FISICO, livros("777", "RUIM", 1)));
        assertEquals(StatusDoacao.REGISTRADA, pendente.getStatus());

        Doacao reavaliada = ctx.doacaoFacade.avaliarDoacao(pendente.getId());
        assertEquals(StatusDoacao.RECUSADA, reavaliada.getStatus());

        Doacao outra = ctx.doacaoService.registrar(new DoacaoDTO(pessoa.getId(), TipoDoacao.LIVRO_FISICO, livros("778", "RUIM", 1)));
        Doacao aceita = ctx.doacaoFacade.aceitarDoacao(outra.getId());
        assertEquals(StatusDoacao.ACEITA, aceita.getStatus());
        assertEquals(1, ctx.acervoService.listarItens().size());
        assertThrows(NegocioException.class, () -> ctx.doacaoFacade.aceitarDoacao(outra.getId()));
        assertThrows(NegocioException.class, () -> ctx.doacaoFacade.recusarDoacao(outra.getId(), "tarde"));

        Doacao terceira = ctx.doacaoService.registrar(new DoacaoDTO(pessoa.getId(), TipoDoacao.DINHEIRO, Map.of("valor", "5", "meioPagamento", "PIX")));
        assertThrows(ValidacaoException.class, () -> ctx.doacaoFacade.recusarDoacao(terceira.getId(), " "));
        assertEquals(StatusDoacao.RECUSADA, ctx.doacaoFacade.recusarDoacao(terceira.getId(), "valor simbólico").getStatus());
    }

    @Test
    void exemplarDoadoVaiParaOPrimeiroDaFilaQuandoHaReservas() {
        UsuarioSite ana = novoUsuarioSite("ana@x.com");
        UsuarioSite beto = novoUsuarioSite("beto@x.com");
        Livro livro = novoLivro("Dom Casmurro", "9780000000011");
        ExemplarFisico unico = novoExemplar(livro.getId(), "T1");
        ctx.emprestimoFacade.emprestar(ana.getId(), unico.getId());
        ctx.reservaService.criar(beto.getId(), livro.getId());

        doarLivros(pessoa, "9780000000011", "BOM", 1);

        ItemAcervo novo = ctx.acervoService.listarItensPorLivro(livro.getId()).stream().filter(i -> !i.getId().equals(unico.getId())).findFirst().orElseThrow();
        assertEquals(StatusItem.RESERVADO, novo.getStatus());
        assertEquals(1, ctx.notificacaoRepositorio.listarPorUsuario(beto.getId()).size());
        assertDoesNotThrow(() -> ctx.emprestimoFacade.emprestar(beto.getId(), novo.getId()));
    }

    @Test
    void crudDeDoacaoRespeitaOStatus() {
        Doacao pendente = ctx.doacaoService.registrar(new DoacaoDTO(pessoa.getId(), TipoDoacao.DINHEIRO, Map.of("valor", "5", "meioPagamento", "PIX")));
        DoacaoDinheiro alterada = (DoacaoDinheiro) ctx.doacaoService.atualizar(pendente.getId(), Map.of("valor", "7,50"));
        assertEquals(new BigDecimal("7.50"), alterada.getValor());
        assertThrows(ValidacaoException.class, () -> ctx.doacaoService.atualizar(pendente.getId(), Map.of("valor", "-1")));

        Doacao aceita = doarDinheiro(pessoa, "20");
        assertThrows(NegocioException.class, () -> ctx.doacaoService.atualizar(aceita.getId(), Map.of("valor", "1")));
        assertThrows(NegocioException.class, () -> ctx.doacaoService.remover(aceita.getId()));
        ctx.doacaoService.remover(pendente.getId());
        assertThrows(NaoEncontradoException.class, () -> ctx.doacaoService.buscar(pendente.getId()));
        assertEquals(1, ctx.doacaoService.listarPorDoador(pessoa.getId()).size());
        assertEquals(1, ctx.doacaoService.listarPorStatus(StatusDoacao.ACEITA).size());
    }
}
