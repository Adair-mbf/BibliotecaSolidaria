package br.com.bibliotecasolidaria.model;

import java.time.LocalDate;

public class Reserva {
    private Long id;
    private Long usuarioId;
    private Long livroId;
    private LocalDate dataReserva;
    private StatusReserva status;

    public Reserva() {}

    public Reserva(Long id, Long usuarioId, Long livroId, LocalDate dataReserva, StatusReserva status) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.livroId = livroId;
        this.dataReserva = dataReserva;
        this.status = status != null ? status : StatusReserva.ATIVA;
    }

    public void atender() {
        this.status = StatusReserva.ATENDIDA;
    }

    public void cancelar() {
        this.status = StatusReserva.CANCELADA;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public Long getLivroId() { return livroId; }
    public void setLivroId(Long livroId) { this.livroId = livroId; }
    public LocalDate getDataReserva() { return dataReserva; }
    public void setDataReserva(LocalDate dataReserva) { this.dataReserva = dataReserva; }
    public StatusReserva getStatus() { return status; }
    public void setStatus(StatusReserva status) { this.status = status; }
}
