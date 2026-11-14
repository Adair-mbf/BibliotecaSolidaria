package br.com.bibliotecasolidaria.model;

import br.com.bibliotecasolidaria.exception.NegocioException;
import br.com.bibliotecasolidaria.model.enums.StatusReserva;
import java.time.LocalDate;

/** Reserva de um livro sem exemplar disponível; forma uma fila por data. */
public class Reserva {
    private Long id;
    private Long usuarioId;
    private Long livroId;
    private LocalDate dataReserva;
    private StatusReserva status;

    public Reserva(Long id, Long usuarioId, Long livroId, LocalDate dataReserva, StatusReserva status) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.livroId = livroId;
        this.dataReserva = dataReserva;
        this.status = status;
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

    public void atender() {
        exigirAtiva();
        status = StatusReserva.ATENDIDA;
    }

    public void cancelar() {
        exigirAtiva();
        status = StatusReserva.CANCELADA;
    }

    private void exigirAtiva() {
        if (status != StatusReserva.ATIVA) {
            throw new NegocioException("A reserva não está ativa (status " + status + ").");
        }
    }

    @Override
    public String toString() {
        return "Reserva{" + "id=" + getId() + ", " + "usuarioId=" + getUsuarioId() + ", " + "livroId=" + getLivroId() + ", " + "dataReserva=" + getDataReserva() + ", " + "status=" + getStatus() + "}";
    }
}
