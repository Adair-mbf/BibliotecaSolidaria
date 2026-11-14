package br.com.bibliotecasolidaria.strategy;

import br.com.bibliotecasolidaria.exception.NegocioException;
import br.com.bibliotecasolidaria.model.Emprestimo;
import br.com.bibliotecasolidaria.model.enums.StatusEmprestimo;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

class RenovacaoStrategyTest {
    private final Clock clock = Clock.fixed(Instant.parse("2026-11-10T12:00:00Z"), ZoneOffset.UTC);
    private final LimiteFixoRenovacao politica = new LimiteFixoRenovacao(2, 14, clock);

    private Emprestimo emprestimo(LocalDate prazo, int renovacoes, StatusEmprestimo status) {
        return new Emprestimo(1L, 1L, 1L, LocalDate.of(2026, 10, 1), prazo, null, renovacoes, status);
    }

    @Test
    void permiteRenovarAteOLimite() {
        LocalDate prazo = LocalDate.of(2026, 11, 15);
        assertTrue(politica.podeRenovar(emprestimo(prazo, 0, StatusEmprestimo.ATIVO)));
        assertTrue(politica.podeRenovar(emprestimo(prazo, 1, StatusEmprestimo.ATIVO)));
        assertFalse(politica.podeRenovar(emprestimo(prazo, 2, StatusEmprestimo.ATIVO)));
    }

    @Test
    void naoRenovaEmprestimoAtrasadoOuDevolvido() {
        assertFalse(politica.podeRenovar(emprestimo(LocalDate.of(2026, 11, 9), 0, StatusEmprestimo.ATIVO)));
        assertFalse(politica.podeRenovar(emprestimo(LocalDate.of(2026, 11, 15), 0, StatusEmprestimo.ATRASADO)));
        assertFalse(politica.podeRenovar(emprestimo(LocalDate.of(2026, 11, 15), 0, StatusEmprestimo.DEVOLVIDO)));
    }

    @Test
    void novoPrazoSomaOsDiasDeRenovacaoAoPrazoAtual() {
        assertEquals(LocalDate.of(2026, 11, 29),
                politica.calcularNovoPrazo(emprestimo(LocalDate.of(2026, 11, 15), 0, StatusEmprestimo.ATIVO)));
    }

    @Test
    void semRenovacaoNuncaPermite() {
        SemRenovacao s = new SemRenovacao();
        Emprestimo e = emprestimo(LocalDate.of(2026, 11, 15), 0, StatusEmprestimo.ATIVO);
        assertFalse(s.podeRenovar(e));
        assertThrows(NegocioException.class, () -> s.calcularNovoPrazo(e));
    }
}
