package br.com.bibliotecasolidaria.observer;

import br.com.bibliotecasolidaria.model.Notificacao;
import br.com.bibliotecasolidaria.persistence.NotificacaoRepositorio;

/** Guarda a notificação no banco para consulta no painel do usuário. */
public class CanalPainelInterno implements CanalNotificacao {
    private final NotificacaoRepositorio notificacoes;

    public CanalPainelInterno(NotificacaoRepositorio notificacoes) {
        this.notificacoes = notificacoes;
    }

    @Override
    public void enviar(Notificacao notificacao) {
        notificacoes.salvar(notificacao);
    }
}
