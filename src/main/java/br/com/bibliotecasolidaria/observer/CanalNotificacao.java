package br.com.bibliotecasolidaria.observer;

import br.com.bibliotecasolidaria.model.Notificacao;

/** Canal de entrega de uma notificação. */
public interface CanalNotificacao {
    void enviar(Notificacao notificacao);
}
