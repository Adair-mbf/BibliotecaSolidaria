package br.com.bibliotecasolidaria;

import br.com.bibliotecasolidaria.model.*;
import br.com.bibliotecasolidaria.strategy.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class StrategyAndFactoryTest {

    @Test
    @DisplayName("Teste da estratégia de multa progressiva com teto")
    void testMultaProgressiva() {
        List<FaixaMulta> faixas = List.of(
            new FaixaMulta(1, 3, new BigDecimal("2.00")),
            new FaixaMulta(4, 10, new BigDecimal("5.00"))
        );
        MultaProgressiva strategy = new MultaProgressiva(faixas, new BigDecimal("20.00"));

        Emprestimo emp = new Emprestimo();
        BigDecimal valor = strategy.calcular(5, emp);
        assertEquals(new BigDecimal("16.00"), valor);

        BigDecimal valorComTeto = strategy.calcular(10, emp);
        assertEquals(new BigDecimal("20.00"), valorComTeto);
    }

    @Test
    @DisplayName("Teste de Bonificação por doação empresarial")
    void testBonificacaoEmpresarial() {
        BonificacaoEmpresarial strategy = new BonificacaoEmpresarial(10);
        Doador empresa = new Doador(1L, "Empresa XPTO", "contato@xpto.com", "123", true, "12345678000199", true);
        DoacaoLivroFisico doacao = new DoacaoLivroFisico(null, 1L, LocalDate.now(), StatusDoacao.REGISTRADA,
                "Java", "Autor", "ISBN", CategoriaLivro.DIDATICO, EstadoConservacao.OTIMO, 15);

        Optional<Bonificacao> bonificacao = strategy.avaliar(empresa, doacao);
        assertTrue(bonificacao.isPresent());
        assertEquals(TipoBonificacao.PARCERIA, bonificacao.get().getTipo());
    }
}
