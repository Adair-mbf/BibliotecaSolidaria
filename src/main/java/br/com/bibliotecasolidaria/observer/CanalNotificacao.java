package br.com.bibliotecasolidaria.observer;

import br.com.bibliotecasolidaria.model.Notificacao;

public interface CanalNotificacao {
    void enviar(Notificacao notificacao);
}
