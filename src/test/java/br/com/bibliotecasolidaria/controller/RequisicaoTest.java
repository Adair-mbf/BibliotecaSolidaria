package br.com.bibliotecasolidaria.controller;

import br.com.bibliotecasolidaria.exception.ValidacaoException;
import br.com.bibliotecasolidaria.model.enums.TipoUsuario;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class RequisicaoTest {
    @Test
    void textoObrigatorioLancaValidacaoQuandoAusenteOuEmBranco() {
        Requisicao r = Requisicao.de("a", "  ", "b", "x");
        assertThrows(ValidacaoException.class, () -> r.texto("a"));
        assertThrows(ValidacaoException.class, () -> r.texto("c"));
        assertEquals("x", r.texto("b"));
        assertNull(r.textoOpcional("a"));
    }

    @Test
    void conversoesNumericasEDeData() {
        Requisicao r = Requisicao.de("n", "42", "d", "12,50", "data", "2026-11-10", "ruim", "abc");
        assertEquals(42L, r.longo("n"));
        assertEquals(42, r.inteiro("n"));
        assertEquals(new BigDecimal("12.50"), r.decimal("d"));
        assertEquals(LocalDate.of(2026, 11, 10), r.data("data"));
        assertNull(r.longoOpcional("zzz"));
        assertThrows(ValidacaoException.class, () -> r.longo("ruim"));
        assertThrows(ValidacaoException.class, () -> r.inteiro("ruim"));
        assertThrows(ValidacaoException.class, () -> r.decimal("ruim"));
        assertThrows(ValidacaoException.class, () -> r.data("ruim"));
    }

    @Test
    void enumeracaoAceitaQualquerCaixaERejeitaValorInvalido() {
        Requisicao r = Requisicao.de("t", "doador", "x", "nada");
        assertEquals(TipoUsuario.DOADOR, r.enumeracao("t", TipoUsuario.class));
        assertThrows(ValidacaoException.class, () -> r.enumeracao("x", TipoUsuario.class));
        assertNull(r.enumeracaoOpcional("y", TipoUsuario.class));
    }

    @Test
    void booleanoAceitaSimNao() {
        Requisicao r = Requisicao.de("a", "sim", "b", "false", "c", "talvez");
        assertTrue(r.booleano("a"));
        assertFalse(r.booleano("b"));
        assertThrows(ValidacaoException.class, () -> r.booleano("c"));
        assertNull(r.booleanoOpcional("d"));
    }
}
