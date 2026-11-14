package br.com.bibliotecasolidaria.observer;

import java.time.LocalDate;
import java.util.List;

/** Avisa que um empréstimo está perto do vencimento. */
public class NotificadorVencimento extends NotificadorBase {
    public NotificadorVencimento(List<CanalNotificacao> canais) {
        super(canais);
    }

    public static String mensagem(String titulo, LocalDate prazo, long diasRestantes) {
        return "O empréstimo do livro \"" + titulo + "\" vence em " + prazo + " ("
                + (diasRestantes == 0 ? "hoje" : "em " + diasRestantes + " dia(s)") + "). Renove ou devolva.";
    }

    @Override
    protected String montarMensagem(EventoBiblioteca evento) {
        return mensagem(String.valueOf(evento.dados().get("livroTitulo")),
                (LocalDate) evento.dados().get("prazo"), ((Number) evento.dados().get("diasRestantes")).longValue());
    }
}
