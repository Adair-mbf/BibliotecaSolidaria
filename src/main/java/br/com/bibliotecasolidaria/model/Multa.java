package br.com.bibliotecasolidaria.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Multa {
    private Long id;
    private Long emprestimoId;
    private Long usuarioId;
    private BigDecimal valor;
    private LocalDate dataGeracao;
    private LocalDate dataPagamento;
    private StatusMulta status;

    public Multa() {}

    public Multa(Long id, Long emprestimoId, Long usuarioId, BigDecimal valor, LocalDate dataGeracao, LocalDate dataPagamento, StatusMulta status) {
        this.id = id;
        this.emprestimoId = emprestimoId;
        this.usuarioId = usuarioId;
        this.valor = valor;
        this.dataGeracao = dataGeracao;
        this.dataPagamento = dataPagamento;
        this.status = status != null ? status : StatusMulta.PENDENTE;
    }

    public void quitar() {
        this.status = StatusMulta.PAGA;
        this.dataPagamento = LocalDate.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEmprestimoId() { return emprestimoId; }
    public void setEmprestimoId(Long emprestimoId) { this.emprestimoId = emprestimoId; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public LocalDate getDataGeracao() { return dataGeracao; }
    public void setDataGeracao(LocalDate dataGeracao) { this.dataGeracao = dataGeracao; }
    public LocalDate getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(LocalDate dataPagamento) { this.dataPagamento = dataPagamento; }
    public StatusMulta getStatus() { return status; }
    public void setStatus(StatusMulta status) { this.status = status; }
}
