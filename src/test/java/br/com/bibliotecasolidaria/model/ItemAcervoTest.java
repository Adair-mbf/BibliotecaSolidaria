package br.com.bibliotecasolidaria.model;

import br.com.bibliotecasolidaria.exception.NegocioException;
import br.com.bibliotecasolidaria.model.enums.EstadoConservacao;
import br.com.bibliotecasolidaria.model.enums.StatusItem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemAcervoTest {
    private ExemplarFisico exemplar() {
        return new ExemplarFisico(1L, 1L, StatusItem.DISPONIVEL, "T1", EstadoConservacao.BOM, "E1");
    }

    @Test
    void exemplarSegueOCicloDisponivelEmprestadoDisponivel() {
        ExemplarFisico e = exemplar();
        assertTrue(e.estaDisponivel());
        e.marcarEmprestado();
        assertEquals(StatusItem.EMPRESTADO, e.getStatus());
        assertFalse(e.estaDisponivel());
        e.marcarDisponivel();
        assertTrue(e.estaDisponivel());
    }

    @Test
    void naoEmprestaExemplarJaEmprestado() {
        ExemplarFisico e = exemplar();
        e.marcarEmprestado();
        assertThrows(NegocioException.class, e::marcarEmprestado);
    }

    @Test
    void exemplarReservadoPodeSerEmprestado() {
        ExemplarFisico e = exemplar();
        e.marcarReservado();
        assertFalse(e.estaDisponivel());
        e.marcarEmprestado();
        assertEquals(StatusItem.EMPRESTADO, e.getStatus());
    }

    @Test
    void ebookEstaSempreDisponivelESeuStatusNaoMuda() {
        Ebook b = new Ebook(1L, 1L, StatusItem.DISPONIVEL, "http://x/a.pdf", "PDF");
        b.marcarEmprestado();
        b.marcarReservado();
        assertTrue(b.estaDisponivel());
        assertEquals(StatusItem.DISPONIVEL, b.getStatus());
    }
}
