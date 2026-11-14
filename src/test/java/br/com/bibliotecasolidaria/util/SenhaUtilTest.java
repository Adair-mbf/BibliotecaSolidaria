package br.com.bibliotecasolidaria.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SenhaUtilTest {
    @Test
    void hashNaoContemASenhaEEhDiferenteACadaChamada() {
        String h1 = SenhaUtil.gerarHash("segredo123");
        String h2 = SenhaUtil.gerarHash("segredo123");
        assertFalse(h1.contains("segredo123"));
        assertNotEquals(h1, h2, "o sal deve variar");
    }

    @Test
    void verificaSenhaCorretaEIncorreta() {
        String hash = SenhaUtil.gerarHash("segredo123");
        assertTrue(SenhaUtil.verificar("segredo123", hash));
        assertFalse(SenhaUtil.verificar("outra", hash));
    }

    @Test
    void entradasNulasOuMalformadasNaoAutenticam() {
        assertFalse(SenhaUtil.verificar(null, SenhaUtil.gerarHash("x")));
        assertFalse(SenhaUtil.verificar("x", null));
        assertFalse(SenhaUtil.verificar("x", "texto-puro"));
        assertFalse(SenhaUtil.verificar("x", "pbkdf2$abc$@@@$@@@"));
    }
}
