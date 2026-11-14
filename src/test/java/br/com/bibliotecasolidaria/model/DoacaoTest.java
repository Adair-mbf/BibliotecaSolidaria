package br.com.bibliotecasolidaria.model;

import br.com.bibliotecasolidaria.exception.NegocioException;
import br.com.bibliotecasolidaria.exception.ValidacaoException;
import br.com.bibliotecasolidaria.model.enums.CategoriaLivro;
import br.com.bibliotecasolidaria.model.enums.EstadoConservacao;
import br.com.bibliotecasolidaria.model.enums.StatusDoacao;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DoacaoTest {
    private static final LocalDate HOJE = LocalDate.of(2026, 11, 10);

    private DoacaoLivroFisico livro(EstadoConservacao estado, int qtd, String isbn) {
        return new DoacaoLivroFisico(null, 1L, HOJE, StatusDoacao.REGISTRADA, "Título", "Autor", isbn,
                CategoriaLivro.INFANTIL, estado, qtd);
    }

    @Test
    void livroFisicoAceitaEstadoMinimoOuMelhor() {
        assertTrue(livro(EstadoConservacao.OTIMO, 1, "111").avaliar());
        assertTrue(livro(EstadoConservacao.ACEITAVEL, 1, "111").avaliar());
        assertFalse(livro(EstadoConservacao.RUIM, 1, "111").avaliar());
    }

    @Test
    void livroFisicoExigeQuantidadeEIsbn() {
        assertFalse(livro(EstadoConservacao.BOM, 0, "111").avaliar());
        assertFalse(livro(EstadoConservacao.BOM, 1, " ").avaliar());
    }

    @Test
    void ebookExigeFormatoSuportadoEArquivo() {
        DoacaoEbook e = new DoacaoEbook(null, 1L, HOJE, StatusDoacao.REGISTRADA, "T", "A", "http://x/a.pdf", "pdf");
        assertTrue(e.avaliar());
        e.setFormato("DOC");
        assertFalse(e.avaliar());
        e.setFormato("EPUB");
        e.setArquivoUrl(null);
        assertFalse(e.avaliar());
    }

    @Test
    void dinheiroExigeValorPositivoEMeioDePagamento() {
        DoacaoDinheiro d = new DoacaoDinheiro(null, 1L, HOJE, StatusDoacao.REGISTRADA, new BigDecimal("10"), "PIX", null);
        assertTrue(d.avaliar());
        d.setValor(BigDecimal.ZERO);
        assertFalse(d.avaliar());
        d.setValor(new BigDecimal("5"));
        d.setMeioPagamento("");
        assertFalse(d.avaliar());
    }

    @Test
    void aceitarMudaStatusEImpedeNovaDecisao() {
        DoacaoLivroFisico d = livro(EstadoConservacao.BOM, 1, "111");
        d.aceitar();
        assertEquals(StatusDoacao.ACEITA, d.getStatus());
        assertTrue(d.estaFinalizada());
        assertThrows(NegocioException.class, d::aceitar);
        assertThrows(NegocioException.class, () -> d.recusar("tarde demais"));
    }

    @Test
    void recusarExigeMotivo() {
        DoacaoLivroFisico d = livro(EstadoConservacao.BOM, 1, "111");
        assertThrows(ValidacaoException.class, () -> d.recusar("  "));
        d.recusar("fora do perfil");
        assertEquals(StatusDoacao.RECUSADA, d.getStatus());
    }

    @Test
    void iniciarAvaliacaoMudaParaEmAvaliacao() {
        DoacaoLivroFisico d = livro(EstadoConservacao.BOM, 1, "111");
        d.iniciarAvaliacao();
        assertEquals(StatusDoacao.EM_AVALIACAO, d.getStatus());
        assertFalse(d.estaFinalizada());
    }
}
