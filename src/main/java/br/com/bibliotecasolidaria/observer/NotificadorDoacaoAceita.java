package br.com.bibliotecasolidaria.observer;

import br.com.bibliotecasolidaria.model.Bonificacao;

import java.util.List;

/** Agradece ao doador e informa as bonificações recebidas. */
public class NotificadorDoacaoAceita extends NotificadorBase {
    public NotificadorDoacaoAceita(List<CanalNotificacao> canais) {
        super(canais);
    }

    @Override
    protected String montarMensagem(EventoBiblioteca evento) {
        StringBuilder sb = new StringBuilder("Sua doação foi aceita. Muito obrigado!");
        Object bonus = evento.dados().get("bonificacoes");
        if (bonus instanceof List<?> lista && !lista.isEmpty()) {
            sb.append(" Bonificações recebidas:");
            for (Object b : lista) {
                sb.append(" ").append(b instanceof Bonificacao bon ? bon.getDescricao() : b).append(";");
            }
        }
        return sb.toString();
    }
}
