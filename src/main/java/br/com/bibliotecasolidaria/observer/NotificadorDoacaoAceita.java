package br.com.bibliotecasolidaria.observer;

import br.com.bibliotecasolidaria.model.Notificacao;
import java.time.LocalDateTime;
import java.util.List;

public class NotificadorDoacaoAceita implements Observador {
    private final List<CanalNotificacao> canais;

    public NotificadorDoacaoAceita(List<CanalNotificacao> canais) {
        this.canais = canais;
    }

    @Override
    public void atualizar(EventoBiblioteca evento) {
        Long uId = evento.getDestinatario() != null ? evento.getDestinatario().getId() : null;
        Notificacao n = new Notificacao(null, uId, evento.getTipo(), evento.getMensagem(), LocalDateTime.now(), false);
        for (CanalNotificacao canal : canais) {
            canal.enviar(n);
        }
    }
}
