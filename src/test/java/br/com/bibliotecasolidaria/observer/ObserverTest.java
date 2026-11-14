package br.com.bibliotecasolidaria.observer;

import br.com.bibliotecasolidaria.model.Bonificacao;
import br.com.bibliotecasolidaria.model.Doador;
import br.com.bibliotecasolidaria.model.Notificacao;
import br.com.bibliotecasolidaria.model.enums.TipoBonificacao;
import br.com.bibliotecasolidaria.model.enums.TipoEvento;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class ObserverTest {
    private static final LocalDateTime AGORA = LocalDateTime.of(2026, 11, 10, 12, 0);
    private final Doador usuario = new Doador(5L, "Ana", "ana@x.com", "h", true, "1", false);
    private final List<Notificacao> enviadas = new ArrayList<>();
    private final CanalNotificacao canal = enviadas::add;

    @Test
    void publicadorEntregaApenasAosObservadoresDoTipo() {
        PublicadorEventos p = new PublicadorEventos();
        List<EventoBiblioteca> a = new ArrayList<>();
        List<EventoBiblioteca> b = new ArrayList<>();
        p.registrar(TipoEvento.LIVRO_DISPONIVEL, a::add);
        p.registrar(TipoEvento.DOACAO_ACEITA, b::add);
        p.notificar(new EventoBiblioteca(TipoEvento.LIVRO_DISPONIVEL, usuario, Map.of(), AGORA));
        assertEquals(1, a.size());
        assertTrue(b.isEmpty());
    }

    @Test
    void removerObservadorInterrompeAsEntregas() {
        PublicadorEventos p = new PublicadorEventos();
        List<EventoBiblioteca> a = new ArrayList<>();
        Observador o = a::add;
        p.registrar(TipoEvento.LIVRO_DISPONIVEL, o);
        p.remover(TipoEvento.LIVRO_DISPONIVEL, o);
        p.notificar(new EventoBiblioteca(TipoEvento.LIVRO_DISPONIVEL, usuario, Map.of(), AGORA));
        assertTrue(a.isEmpty());
    }

    @Test
    void falhaDeUmObservadorNaoImpedeOsDemais() {
        Logger log = Logger.getLogger(PublicadorEventos.class.getName());
        Level anterior = log.getLevel();
        log.setLevel(Level.OFF);
        try {
            PublicadorEventos p = new PublicadorEventos();
            List<EventoBiblioteca> recebidos = new ArrayList<>();
            p.registrar(TipoEvento.VENCIMENTO_PROXIMO, e -> { throw new IllegalStateException("boom"); });
            p.registrar(TipoEvento.VENCIMENTO_PROXIMO, recebidos::add);
            p.notificar(new EventoBiblioteca(TipoEvento.VENCIMENTO_PROXIMO, usuario, Map.of(), AGORA));
            assertEquals(1, recebidos.size());
        } finally {
            log.setLevel(anterior);
        }
    }

    @Test
    void notificadorDeDisponibilidadeCitaOLivro() {
        new NotificadorDisponibilidade(List.of(canal)).atualizar(new EventoBiblioteca(TipoEvento.LIVRO_DISPONIVEL,
                usuario, Map.of("livroTitulo", "Dom Casmurro"), AGORA));
        assertEquals(1, enviadas.size());
        Notificacao n = enviadas.get(0);
        assertEquals(5L, n.getUsuarioId());
        assertEquals(TipoEvento.LIVRO_DISPONIVEL, n.getTipo());
        assertTrue(n.getMensagem().contains("Dom Casmurro"));
        assertFalse(n.isLida());
        assertEquals(AGORA, n.getDataEnvio());
    }

    @Test
    void notificadorDeVencimentoInformaPrazoEDias() {
        new NotificadorVencimento(List.of(canal)).atualizar(new EventoBiblioteca(TipoEvento.VENCIMENTO_PROXIMO,
                usuario, Map.of("livroTitulo", "O Alienista", "prazo", LocalDate.of(2026, 11, 12), "diasRestantes", 2L), AGORA));
        String m = enviadas.get(0).getMensagem();
        assertTrue(m.contains("O Alienista") && m.contains("2026-11-12") && m.contains("2 dia"));
    }

    @Test
    void notificadorDeDoacaoListaAsBonificacoes() {
        Bonificacao b = new Bonificacao(1L, 5L, TipoBonificacao.CERTIFICADO, "Certificado por doar 10 livros", AGORA.toLocalDate());
        new NotificadorDoacaoAceita(List.of(canal)).atualizar(new EventoBiblioteca(TipoEvento.DOACAO_ACEITA,
                usuario, Map.of("bonificacoes", List.of(b)), AGORA));
        assertTrue(enviadas.get(0).getMensagem().contains("Certificado por doar 10 livros"));
    }

    @Test
    void mensagemExplicitaDoEventoTemPrioridadeESemDestinatarioNadaEhEnviado() {
        NotificadorDisponibilidade n = new NotificadorDisponibilidade(List.of(canal));
        n.atualizar(new EventoBiblioteca(TipoEvento.LIVRO_DISPONIVEL, usuario, "texto fixo", Map.of(), AGORA));
        assertEquals("texto fixo", enviadas.get(0).getMensagem());
        n.atualizar(new EventoBiblioteca(TipoEvento.LIVRO_DISPONIVEL, null, "x", Map.of(), AGORA));
        assertEquals(1, enviadas.size());
    }
}
