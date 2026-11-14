package br.com.bibliotecasolidaria.observer;

import java.util.List;

/** Avisa o usuário no topo da fila de reservas que o livro ficou disponível. */
public class NotificadorDisponibilidade extends NotificadorBase {
    public NotificadorDisponibilidade(List<CanalNotificacao> canais) {
        super(canais);
    }

    @Override
    protected String montarMensagem(EventoBiblioteca evento) {
        return "O livro \"" + evento.dados().get("livroTitulo")
                + "\" que você reservou está disponível para empréstimo.";
    }
}
