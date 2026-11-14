package br.com.bibliotecasolidaria.adapter;

import br.com.bibliotecasolidaria.dto.DadosLivro;
import br.com.bibliotecasolidaria.exception.NegocioException;
import br.com.bibliotecasolidaria.model.ComprovantePagamento;
import br.com.bibliotecasolidaria.model.Doador;
import br.com.bibliotecasolidaria.model.Multa;
import br.com.bibliotecasolidaria.model.enums.StatusMulta;
import br.com.bibliotecasolidaria.model.enums.StatusPagamento;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AdapterTest {
    private final Doador pagador = new Doador(1L, "Ana", "ana@x.com", "h", true, "1", false);
    private final Multa multa = new Multa(9L, 1L, new BigDecimal("12.34"), LocalDate.of(2026, 11, 10), null, StatusMulta.PENDENTE);

    private ApiPagamentoExterna apiComStatus(String status, List<Long> centavosRecebidos) {
        return new ApiPagamentoExterna() {
            @Override public String criarCobranca(long valorCentavos, String descricao, String email) {
                centavosRecebidos.add(valorCentavos);
                return "REF-1";
            }
            @Override public String verificarCobranca(String referencia) { return status; }
        };
    }

    @Test
    void pagamentoAprovadoConverteReaisParaCentavos() {
        List<Long> centavos = new ArrayList<>();
        ComprovantePagamento c = new PagamentoAdapter(apiComStatus("PAID", centavos)).cobrar(multa, pagador);
        assertEquals(List.of(1234L), centavos);
        assertTrue(c.aprovado());
        assertEquals("REF-1", c.idTransacao());
        assertEquals(new BigDecimal("12.34"), c.valor());
    }

    @Test
    void traduzOsStatusDoProvedor() {
        assertEquals(StatusPagamento.RECUSADO, new PagamentoAdapter(apiComStatus("FAILED", new ArrayList<>())).cobrar(multa, pagador).status());
        assertEquals(StatusPagamento.PENDENTE, new PagamentoAdapter(apiComStatus("PENDING", new ArrayList<>())).cobrar(multa, pagador).status());
        assertEquals(StatusPagamento.ESTORNADO, new PagamentoAdapter(apiComStatus("REFUNDED", new ArrayList<>())).cobrar(multa, pagador).status());
        assertEquals(StatusPagamento.PENDENTE, new PagamentoAdapter(apiComStatus("???", new ArrayList<>())).cobrar(multa, pagador).status(),
                "status desconhecido nunca pode virar aprovado");
    }

    @Test
    void simulacaoAprovaECobrancaInexistenteViraErroDeNegocio() {
        PagamentoAdapter adapter = new PagamentoAdapter(new ApiPagamentoExterna());
        ComprovantePagamento c = adapter.cobrar(multa, pagador);
        assertEquals(StatusPagamento.APROVADO, adapter.consultarStatus(c.idTransacao()));
        assertThrows(NegocioException.class, () -> adapter.consultarStatus("TX-INEXISTENTE"));
    }

    @Test
    void cobrancaInvalidaViraErroDeNegocio() {
        Multa zerada = new Multa(1L, 1L, BigDecimal.ZERO, LocalDate.now(), null, StatusMulta.PENDENTE);
        assertThrows(NegocioException.class, () -> new PagamentoAdapter(new ApiPagamentoExterna()).cobrar(zerada, pagador));
    }

    @Test
    void buscaPorIsbnIgnoraHifensEConverteOsCampos() {
        AcervoGlobalAdapter a = new AcervoGlobalAdapter(new ApiAcervoGlobal());
        DadosLivro d = a.buscarPorIsbn("978-0-0000-0001-1").orElseThrow();
        assertEquals("Dom Casmurro", d.titulo());
        assertEquals(List.of("Machado de Assis"), d.autores());
        assertEquals(1899, d.anoPublicacao());
        assertEquals("Editora Simulada", d.editora());
        assertTrue(a.buscarPorIsbn("0000").isEmpty());
        assertTrue(a.buscarPorIsbn(" ").isEmpty());
        assertTrue(a.buscarPorIsbn(null).isEmpty());
    }

    @Test
    void pesquisaPaginaOsResultados() {
        List<String> registros = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            registros.add("isbn=" + i + ";titulo=Livro " + i + ";autores=A|B;ano=2000");
        }
        AcervoGlobalAdapter a = new AcervoGlobalAdapter(new ApiAcervoGlobal(registros));
        assertEquals(5, a.pesquisar("livro", 1).size());
        assertEquals(2, a.pesquisar("livro", 2).size());
        assertTrue(a.pesquisar("livro", 3).isEmpty());
        assertEquals(List.of("A", "B"), a.pesquisar("livro 1", 1).get(0).autores());
        assertTrue(a.pesquisar("inexistente", 1).isEmpty());
    }

    @Test
    void registrosMalformadosSaoIgnorados() {
        AcervoGlobalAdapter a = new AcervoGlobalAdapter(new ApiAcervoGlobal(List.of("lixo", "isbn=1;titulo=Bom;ano=abc")));
        List<DadosLivro> r = a.pesquisar("", 1);
        assertEquals(1, r.size());
        assertEquals(0, r.get(0).anoPublicacao());
    }
}
