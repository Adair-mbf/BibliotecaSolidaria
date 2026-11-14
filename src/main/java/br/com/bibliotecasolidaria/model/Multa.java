package br.com.bibliotecasolidaria.model;

import br.com.bibliotecasolidaria.exception.NegocioException;
import br.com.bibliotecasolidaria.model.enums.StatusMulta;
import java.math.BigDecimal;
import java.time.LocalDate;

/** Multa gerada por atraso na devolução. */
public class Multa {
    private Long id;
    private Long emprestimoId;
    private BigDecimal valor;
    private LocalDate dataGeracao;
    private LocalDate dataPagamento;
    private StatusMulta status;

    public Multa(Long id, Long emprestimoId, BigDecimal valor, LocalDate dataGeracao, LocalDate dataPagamento, StatusMulta status) {
        this.id = id;
        this.emprestimoId = emprestimoId;
        this.valor = valor;
        this.dataGeracao = dataGeracao;
        this.dataPagamento = dataPagamento;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEmprestimoId() { return emprestimoId; }
    public void setEmprestimoId(Long emprestimoId) { this.emprestimoId = emprestimoId; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public LocalDate getDataGeracao() { return dataGeracao; }
    public void setDataGeracao(LocalDate dataGeracao) { this.dataGeracao = dataGeracao; }
    public LocalDate getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(LocalDate dataPagamento) { this.dataPagamento = dataPagamento; }
    public StatusMulta getStatus() { return status; }
    public void setStatus(StatusMulta status) { this.status = status; }

    public void quitar() { quitar(LocalDate.now()); }

    public void quitar(LocalDate data) {
        if (status == StatusMulta.PAGA) {
            throw new NegocioException("A multa já foi paga.");
        }
        dataPagamento = data;
        status = StatusMulta.PAGA;
    }

    @Override
    public String toString() {
        return "Multa{" + "id=" + getId() + ", " + "emprestimoId=" + getEmprestimoId() + ", " + "valor=" + getValor() + ", " + "dataGeracao=" + getDataGeracao() + ", " + "dataPagamento=" + getDataPagamento() + ", " + "status=" + getStatus() + "}";
    }
}
