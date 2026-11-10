package br.com.bibliotecasolidaria.observer;

import br.com.bibliotecasolidaria.model.TipoEvento;
import br.com.bibliotecasolidaria.model.Usuario;
import java.time.LocalDateTime;
import java.util.Map;

public class EventoBiblioteca {
    private TipoEvento tipo;
    private Usuario destinatario;
    private String mensagem;
    private Map<String, Object> dados;
    private LocalDateTime ocorridoEm;

    public EventoBiblioteca(TipoEvento tipo, Usuario destinatario, String mensagem, Map<String, Object> dados) {
        this.tipo = tipo;
        this.destinatario = destinatario;
        this.mensagem = mensagem;
        this.dados = dados;
        this.ocorridoEm = LocalDateTime.now();
    }

    public TipoEvento getTipo() { return tipo; }
    public Usuario getDestinatario() { return destinatario; }
    public String getMensagem() { return mensagem; }
    public Map<String, Object> getDados() { return dados; }
    public LocalDateTime getOcorridoEm() { return ocorridoEm; }
}
