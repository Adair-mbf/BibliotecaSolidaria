package br.com.bibliotecasolidaria.observer;

import br.com.bibliotecasolidaria.model.Notificacao;

import java.util.List;

/** Base dos observadores: converte o evento em {@link Notificacao} e a envia por todos os canais. */
public abstract class NotificadorBase implements Observador {
    private final List<CanalNotificacao> canais;

    protected NotificadorBase(List<CanalNotificacao> canais) {
        this.canais = List.copyOf(canais);
    }

    protected abstract String montarMensagem(EventoBiblioteca evento);

    @Override
    public void atualizar(EventoBiblioteca evento) {
        if (evento.destinatario() == null) {
            return;
        }
        String mensagem = evento.mensagem() != null ? evento.mensagem() : montarMensagem(evento);
        Notificacao n = new Notificacao(null, evento.destinatario().getId(), evento.tipo(), mensagem,
                evento.ocorridoEm(), false);
        canais.forEach(c -> c.enviar(n));
    }
}
