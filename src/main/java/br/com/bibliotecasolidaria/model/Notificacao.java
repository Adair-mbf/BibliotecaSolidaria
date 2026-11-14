package br.com.bibliotecasolidaria.model;

import br.com.bibliotecasolidaria.model.enums.TipoEvento;
import java.time.LocalDateTime;

/** Aviso enviado a um usuário. */
public class Notificacao {
    private Long id;
    private Long usuarioId;
    private TipoEvento tipo;
    private String mensagem;
    private LocalDateTime dataEnvio;
    private boolean lida;

    public Notificacao(Long id, Long usuarioId, TipoEvento tipo, String mensagem, LocalDateTime dataEnvio, boolean lida) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.tipo = tipo;
        this.mensagem = mensagem;
        this.dataEnvio = dataEnvio;
        this.lida = lida;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public TipoEvento getTipo() { return tipo; }
    public void setTipo(TipoEvento tipo) { this.tipo = tipo; }
    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
    public LocalDateTime getDataEnvio() { return dataEnvio; }
    public void setDataEnvio(LocalDateTime dataEnvio) { this.dataEnvio = dataEnvio; }
    public boolean isLida() { return lida; }
    public void setLida(boolean lida) { this.lida = lida; }

    public void marcarComoLida() { lida = true; }

    @Override
    public String toString() {
        return "Notificacao{" + "id=" + getId() + ", " + "usuarioId=" + getUsuarioId() + ", " + "tipo=" + getTipo() + ", " + "mensagem='" + getMensagem() + "'" + ", " + "dataEnvio=" + getDataEnvio() + ", " + "lida=" + isLida() + "}";
    }
}
