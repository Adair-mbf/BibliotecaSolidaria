package br.com.bibliotecasolidaria.observer;

import br.com.bibliotecasolidaria.model.Notificacao;
import java.util.ArrayList;
import java.util.List;

public class CanalPainelInterno implements CanalNotificacao {
    private final List<Notificacao> notificacoesInternas = new ArrayList<>();

    @Override
    public void enviar(Notificacao notificacao) {
        notificacoesInternas.add(notificacao);
    }

    public List<Notificacao> getNotificacoesInternas() { return notificacoesInternas; }
}
