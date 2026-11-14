package br.com.bibliotecasolidaria.model;

import br.com.bibliotecasolidaria.exception.NegocioException;
import br.com.bibliotecasolidaria.exception.ValidacaoException;
import br.com.bibliotecasolidaria.model.enums.TipoDoacao;
import br.com.bibliotecasolidaria.model.enums.StatusDoacao;
import java.time.LocalDate;

/** Doação registrada por um doador (classe base dos três tipos de doação). */
public abstract class Doacao {
    private Long id;
    private Long doadorId;
    private LocalDate dataRegistro;
    private StatusDoacao status;

    protected Doacao(Long id, Long doadorId, LocalDate dataRegistro, StatusDoacao status) {
        this.id = id;
        this.doadorId = doadorId;
        this.dataRegistro = dataRegistro;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getDoadorId() { return doadorId; }
    public void setDoadorId(Long doadorId) { this.doadorId = doadorId; }
    public LocalDate getDataRegistro() { return dataRegistro; }
    public void setDataRegistro(LocalDate dataRegistro) { this.dataRegistro = dataRegistro; }
    public StatusDoacao getStatus() { return status; }
    public void setStatus(StatusDoacao status) { this.status = status; }

    /** Aplica a regra de avaliação do tipo de doação, sem alterar o status. */
    public abstract boolean avaliar();

    public abstract TipoDoacao getTipo();

    public void iniciarAvaliacao() {
        exigirPendente();
        status = StatusDoacao.EM_AVALIACAO;
    }

    public void aceitar() {
        exigirPendente();
        status = StatusDoacao.ACEITA;
    }

    public void recusar(String motivo) {
        exigirPendente();
        if (motivo == null || motivo.isBlank()) {
            throw new ValidacaoException("O motivo da recusa é obrigatório.");
        }
        status = StatusDoacao.RECUSADA;
    }

    public boolean estaFinalizada() {
        return status == StatusDoacao.ACEITA || status == StatusDoacao.RECUSADA;
    }

    private void exigirPendente() {
        if (estaFinalizada()) {
            throw new NegocioException("A doação já foi finalizada com status " + status + ".");
        }
    }

    protected static boolean preenchido(String valor) {
        return valor != null && !valor.isBlank();
    }

    @Override
    public String toString() {
        return "Doacao{" + "id=" + getId() + ", " + "doadorId=" + getDoadorId() + ", " + "dataRegistro=" + getDataRegistro() + ", " + "status=" + getStatus() + "}";
    }
}
