package br.com.bibliotecasolidaria.strategy;

import br.com.bibliotecasolidaria.model.Emprestimo;
import br.com.bibliotecasolidaria.model.enums.StatusEmprestimo;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MultaStrategyTest {
    private final Emprestimo emprestimo = new Emprestimo(1L, 1L, 1L, LocalDate.of(2026, 1, 1),
            LocalDate.of(2026, 1, 15), null, 0, StatusEmprestimo.ATIVO);

    @Test
    void fixaMultiplicaValorDiarioPelosDiasDeAtraso() {
        MultaFixaDiaria s = new MultaFixaDiaria(new BigDecimal("1.50"));
        assertEquals(new BigDecimal("4.50"), s.calcular(3, emprestimo));
        assertEquals(new BigDecimal("0.00"), s.calcular(0, emprestimo));
        assertEquals(new BigDecimal("0.00"), s.calcular(-2, emprestimo));
    }

    @Test
    void fixaRejeitaValorNegativo() {
        assertThrows(IllegalArgumentException.class, () -> new MultaFixaDiaria(new BigDecimal("-1")));
    }

    private MultaProgressiva progressiva(BigDecimal teto) {
        return new MultaProgressiva(List.of(new FaixaMulta(4, 6, new BigDecimal("2.00")),
                new FaixaMulta(1, 3, new BigDecimal("1.00"))), teto);
    }

    @Test
    void progressivaSomaOValorDiarioDeCadaFaixa() {
        assertEquals(new BigDecimal("3.00"), progressiva(null).calcular(3, emprestimo));
        assertEquals(new BigDecimal("7.00"), progressiva(null).calcular(5, emprestimo));
    }

    @Test
    void progressivaUsaAUltimaFaixaAlemDelaEAplicaTeto() {
        assertEquals(new BigDecimal("13.00"), progressiva(null).calcular(8, emprestimo)); // 3 + 6 + 2*2
        assertEquals(new BigDecimal("10.00"), progressiva(new BigDecimal("10")).calcular(8, emprestimo));
    }

    @Test
    void progressivaRejeitaFaixasSobrepostasOuVazias() {
        assertThrows(IllegalArgumentException.class, () -> new MultaProgressiva(List.of(
                new FaixaMulta(1, 5, BigDecimal.ONE), new FaixaMulta(5, 8, BigDecimal.ONE)), null));
        assertThrows(IllegalArgumentException.class, () -> new MultaProgressiva(List.of(), null));
    }

    @Test
    void faixaValidaSeusLimites() {
        assertThrows(IllegalArgumentException.class, () -> new FaixaMulta(0, 3, BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class, () -> new FaixaMulta(5, 3, BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class, () -> new FaixaMulta(1, 3, new BigDecimal("-1")));
    }
}
