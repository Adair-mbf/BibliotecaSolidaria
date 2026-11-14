package br.com.bibliotecasolidaria.strategy;

import br.com.bibliotecasolidaria.model.Bonificacao;
import br.com.bibliotecasolidaria.model.DoacaoDinheiro;
import br.com.bibliotecasolidaria.model.DoacaoEbook;
import br.com.bibliotecasolidaria.model.DoacaoLivroFisico;
import br.com.bibliotecasolidaria.model.Doador;
import br.com.bibliotecasolidaria.model.enums.CategoriaLivro;
import br.com.bibliotecasolidaria.model.enums.EstadoConservacao;
import br.com.bibliotecasolidaria.model.enums.StatusDoacao;
import br.com.bibliotecasolidaria.model.enums.TipoBonificacao;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BonificacaoStrategyTest {
    private static final LocalDate HOJE = LocalDate.of(2026, 11, 10);
    private final Doador pessoa = new Doador(7L, "Ana", "a@a", "h", true, "1", false);
    private final Doador empresa = new Doador(8L, "Acme", "b@b", "h", true, "2", true);

    private DoacaoLivroFisico livros(int qtd) {
        return new DoacaoLivroFisico(1L, 7L, HOJE, StatusDoacao.ACEITA, "T", "A", "1", CategoriaLivro.OUTRO,
                EstadoConservacao.BOM, qtd);
    }

    private DoacaoDinheiro dinheiro(String valor) {
        return new DoacaoDinheiro(2L, 7L, HOJE, StatusDoacao.ACEITA, new BigDecimal(valor), "PIX", null);
    }

    @Test
    void porQuantidadeConcedeCertificadoNoLimiteOuAcima() {
        BonificacaoPorQuantidade s = new BonificacaoPorQuantidade(10);
        Optional<Bonificacao> b = s.avaliar(pessoa, livros(10));
        assertTrue(b.isPresent());
        assertEquals(TipoBonificacao.CERTIFICADO, b.get().getTipo());
        assertEquals(7L, b.get().getDoadorId());
        assertEquals(HOJE, b.get().getDataConcessao());
        assertTrue(s.avaliar(pessoa, livros(9)).isEmpty());
        assertTrue(s.avaliar(pessoa, dinheiro("500")).isEmpty());
    }

    @Test
    void porValorConcedeSeloSoParaDinheiroNoLimiteOuAcima() {
        BonificacaoPorValor s = new BonificacaoPorValor(new BigDecimal("100.00"));
        assertEquals(TipoBonificacao.SELO, s.avaliar(pessoa, dinheiro("100.00")).orElseThrow().getTipo());
        assertTrue(s.avaliar(pessoa, dinheiro("99.99")).isEmpty());
        assertTrue(s.avaliar(pessoa, livros(50)).isEmpty());
    }

    @Test
    void empresarialExigePessoaJuridica() {
        BonificacaoEmpresarial s = new BonificacaoEmpresarial(5);
        assertEquals(TipoBonificacao.PARCERIA, s.avaliar(empresa, livros(5)).orElseThrow().getTipo());
        assertTrue(s.avaliar(pessoa, livros(5)).isEmpty());
        assertTrue(s.avaliar(empresa, livros(4)).isEmpty());
    }

    @Test
    void doacaoDeEbookNaoGeraBonificacaoPorQuantidade() {
        DoacaoEbook e = new DoacaoEbook(3L, 7L, HOJE, StatusDoacao.ACEITA, "T", "A", "u", "PDF");
        assertTrue(new BonificacaoPorQuantidade(1).avaliar(pessoa, e).isEmpty());
        assertTrue(new BonificacaoEmpresarial(1).avaliar(empresa, e).isEmpty());
    }
}
