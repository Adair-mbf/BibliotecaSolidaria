package br.com.bibliotecasolidaria.observer;

import br.com.bibliotecasolidaria.model.Notificacao;
import br.com.bibliotecasolidaria.model.Usuario;
import br.com.bibliotecasolidaria.persistence.UsuarioRepositorio;

import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Canal de e-mail <b>simulado</b>: monta a mensagem e a entrega a um {@code transporte}
 * (por padrão, o log). Troque o transporte por um cliente SMTP para envio real.
 */
public class CanalEmail implements CanalNotificacao {
    private static final Logger LOG = Logger.getLogger(CanalEmail.class.getName());

    private final UsuarioRepositorio usuarios;
    private final Consumer<String> transporte;

    public CanalEmail(UsuarioRepositorio usuarios, Consumer<String> transporte) {
        this.usuarios = usuarios;
        this.transporte = transporte;
    }

    public CanalEmail(UsuarioRepositorio usuarios) {
        this(usuarios, LOG::info);
    }

    @Override
    public void enviar(Notificacao notificacao) {
        usuarios.buscarPorId(notificacao.getUsuarioId()).map(Usuario::getEmail).ifPresent(email ->
                transporte.accept("E-mail para " + email + " [" + notificacao.getTipo() + "]: "
                        + notificacao.getMensagem()));
    }
}
