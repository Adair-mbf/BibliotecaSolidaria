package br.com.bibliotecasolidaria.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ComprovantePagamento {
    private String idTransacao;
    private BigDecimal valor;
    private LocalDateTime dataPagamento;
    private StatusPagamento status;

    public ComprovantePagamento() {}

    public ComprovantePagamento(String idTransacao, BigDecimal valor, LocalDateTime dataPagamento, StatusPagamento status) {
        this.idTransacao = idTransacao;
        this.valor = valor;
        this.dataPagamento = dataPagamento;
        this.status = status;
    }

    public String getIdTransacao() { return idTransacao; }
    public void setIdTransacao(String idTransacao) { this.idTransacao = idTransacao; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public LocalDateTime getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(LocalDateTime dataPagamento) { this.dataPagamento = dataPagamento; }
    public StatusPagamento getStatus() { return status; }
    public void setStatus(StatusPagamento status) { this.status = status; }
}
