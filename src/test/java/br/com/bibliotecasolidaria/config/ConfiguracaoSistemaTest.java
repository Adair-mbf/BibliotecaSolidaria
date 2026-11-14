package br.com.bibliotecasolidaria.config;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ConfiguracaoSistemaTest {
    @Test
    void usaValoresPadrao() {
        ConfiguracaoSistema c = ConfiguracaoSistema.criar(Map.of());
        assertEquals(14, c.getDiasEmprestimo());
        assertEquals(2, c.getMaxRenovacoes());
        assertEquals(new BigDecimal("1.00"), c.getValorMultaDia());
        assertEquals(3, c.getDiasAvisoVencimento());
        assertEquals("fixa", c.getEstrategiaMulta());
    }

    @Test
    void sobrescritasTemPrioridade() {
        ConfiguracaoSistema c = ConfiguracaoSistema.criar(Map.of(ConfiguracaoSistema.DIAS_EMPRESTIMO, "21",
                ConfiguracaoSistema.VALOR_MULTA_DIA, "0.75"));
        assertEquals(21, c.getDiasEmprestimo());
        assertEquals(new BigDecimal("0.75"), c.getValorMultaDia());
        assertEquals("21", c.get(ConfiguracaoSistema.DIAS_EMPRESTIMO));
    }

    @Test
    void rejeitaValoresInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> ConfiguracaoSistema.criar(Map.of(ConfiguracaoSistema.DIAS_EMPRESTIMO, "0")));
        assertThrows(IllegalArgumentException.class, () -> ConfiguracaoSistema.criar(Map.of(ConfiguracaoSistema.MAX_RENOVACOES, "abc")));
        assertThrows(IllegalArgumentException.class, () -> ConfiguracaoSistema.criar(Map.of(ConfiguracaoSistema.VALOR_MULTA_DIA, "-1")));
    }

    @Test
    void singletonRetornaSempreAMesmaInstanciaELeOArquivoDeConfiguracao() {
        assertSame(ConfiguracaoSistema.getInstancia(), ConfiguracaoSistema.getInstancia());
        assertTrue(ConfiguracaoSistema.getInstancia().getDiasEmprestimo() > 0);
    }
}
