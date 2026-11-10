package br.com.bibliotecasolidaria.observer;

import br.com.bibliotecasolidaria.model.Notificacao;
import java.util.ArrayList;
import java.util.List;

public class CanalEmail implements CanalNotificacao {
    private final List<Notificacao> enviados = new ArrayList<>();

    @Override
    public void enviar(Notificacao notificacao) {
        enviados.add(notificacao);
    }

    public List<Notificacao> getEnviados() { return enviados; }
}
