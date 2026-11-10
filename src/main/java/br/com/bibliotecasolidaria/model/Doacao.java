package br.com.bibliotecasolidaria.model;

import java.time.LocalDate;

public abstract class Doacao {
    protected Long id;
    protected Long doadorId;
    protected LocalDate dataRegistro;
    protected StatusDoacao status;

    public Doacao() {}

    public Doacao(Long id, Long doadorId, LocalDate dataRegistro, StatusDoacao status) {
        this.id = id;
        this.doadorId = doadorId;
        this.dataRegistro = dataRegistro != null ? dataRegistro : LocalDate.now();
        this.status = status != null ? status : StatusDoacao.REGISTRADA;
    }

    public abstract boolean avaliar();

    public void aceitar() {
        this.status = StatusDoacao.ACEITA;
    }

    public void recusar(String motivo) {
        this.status = StatusDoacao.RECUSADA;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getDoadorId() { return doadorId; }
    public void setDoadorId(Long doadorId) { this.doadorId = doadorId; }
    public LocalDate getDataRegistro() { return dataRegistro; }
    public void setDataRegistro(LocalDate dataRegistro) { this.dataRegistro = dataRegistro; }
    public StatusDoacao getStatus() { return status; }
    public void setStatus(StatusDoacao status) { this.status = status; }
}
