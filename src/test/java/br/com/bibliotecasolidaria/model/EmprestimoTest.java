package br.com.bibliotecasolidaria.model;

import br.com.bibliotecasolidaria.exception.NegocioException;
import br.com.bibliotecasolidaria.exception.ValidacaoException;
import br.com.bibliotecasolidaria.model.enums.StatusEmprestimo;
import br.com.bibliotecasolidaria.model.enums.StatusMulta;
import br.com.bibliotecasolidaria.model.enums.StatusReserva;
import br.com.bibliotecasolidaria.model.enums.TipoEvento;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EmprestimoTest {
    private static final LocalDate DIA0 = LocalDate.of(2026, 11, 1);

    private Emprestimo novo() {
        return new Emprestimo(1L, 1L, 1L, DIA0, DIA0.plusDays(14), null, 0, StatusEmprestimo.ATIVO);
    }

    @Test
    void calculaDiasDeAtrasoNaDataDeReferencia() {
        Emprestimo e = novo();
        assertEquals(0, e.diasAtraso(DIA0.plusDays(14)));
        assertEquals(0, e.diasAtraso(DIA0.plusDays(3)));
        assertEquals(5, e.diasAtraso(DIA0.plusDays(19)));
        assertFalse(e.estaAtrasado(DIA0.plusDays(14)));
        assertTrue(e.estaAtrasado(DIA0.plusDays(15)));
    }

    @Test
    void depoisDeDevolvidoOAtrasoUsaADataDaDevolucao() {
        Emprestimo e = novo();
        e.registrarDevolucao(DIA0.plusDays(17));
        assertEquals(3, e.diasAtraso(DIA0.plusDays(100)));
        assertFalse(e.estaAtrasado(DIA0.plusDays(100)));
        assertEquals(StatusEmprestimo.DEVOLVIDO, e.getStatus());
    }

    @Test
    void naoDevolveDuasVezesNemAntesDoEmprestimo() {
        Emprestimo e = novo();
        assertThrows(ValidacaoException.class, () -> e.registrarDevolucao(DIA0.minusDays(1)));
        e.registrarDevolucao(DIA0.plusDays(2));
        assertThrows(NegocioException.class, () -> e.registrarDevolucao(DIA0.plusDays(3)));
    }

    @Test
    void renovarEstendePrazoEContaRenovacao() {
        Emprestimo e = novo();
        e.renovar(DIA0.plusDays(28));
        assertEquals(DIA0.plusDays(28), e.getDataPrevistaDevolucao());
        assertEquals(1, e.getRenovacoes());
    }

    @Test
    void renovarExigePrazoPosteriorEEmprestimoEmAberto() {
        Emprestimo e = novo();
        assertThrows(ValidacaoException.class, () -> e.renovar(DIA0.plusDays(14)));
        e.registrarDevolucao(DIA0.plusDays(1));
        assertThrows(NegocioException.class, () -> e.renovar(DIA0.plusDays(30)));
    }

    @Test
    void marcarAtrasadoSoAfetaEmprestimosAtivos() {
        Emprestimo e = novo();
        e.marcarAtrasado();
        assertEquals(StatusEmprestimo.ATRASADO, e.getStatus());
        e.registrarDevolucao(DIA0.plusDays(20));
        e.marcarAtrasado();
        assertEquals(StatusEmprestimo.DEVOLVIDO, e.getStatus());
    }

    @Test
    void multaSoPodeSerQuitadaUmaVez() {
        Multa m = new Multa(1L, 1L, new BigDecimal("5.00"), DIA0, null, StatusMulta.PENDENTE);
        m.quitar(DIA0.plusDays(1));
        assertEquals(StatusMulta.PAGA, m.getStatus());
        assertEquals(DIA0.plusDays(1), m.getDataPagamento());
        assertThrows(NegocioException.class, () -> m.quitar(DIA0.plusDays(2)));
    }

    @Test
    void reservaAtivaPodeSerAtendidaOuCanceladaUmaUnicaVez() {
        Reserva r = new Reserva(1L, 1L, 1L, DIA0, StatusReserva.ATIVA);
        r.atender();
        assertEquals(StatusReserva.ATENDIDA, r.getStatus());
        assertThrows(NegocioException.class, r::cancelar);
        Reserva r2 = new Reserva(2L, 1L, 1L, DIA0, StatusReserva.ATIVA);
        r2.cancelar();
        assertThrows(NegocioException.class, r2::atender);
    }

    @Test
    void notificacaoPodeSerMarcadaComoLida() {
        Notificacao n = new Notificacao(null, 1L, TipoEvento.LIVRO_DISPONIVEL, "oi", LocalDateTime.now(), false);
        n.marcarComoLida();
        assertTrue(n.isLida());
    }
}
