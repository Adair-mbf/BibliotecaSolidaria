package br.com.bibliotecasolidaria.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DoacaoDinheiro extends Doacao {
    private BigDecimal valor;
    private String meioPagamento;
    private String comprovante;

    public DoacaoDinheiro() { super(); }

    public DoacaoDinheiro(Long id, Long doadorId, LocalDate dataRegistro, StatusDoacao status,
                          BigDecimal valor, String meioPagamento, String comprovante) {
        super(id, doadorId, dataRegistro, status);
        this.valor = valor;
        this.meioPagamento = meioPagamento;
        this.comprovante = comprovante;
    }

    @Override
    public boolean avaliar() {
        return valor != null && valor.compareTo(BigDecimal.ZERO) > 0;
    }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public String getMeioPagamento() { return meioPagamento; }
    public void setMeioPagamento(String meioPagamento) { this.meioPagamento = meioPagamento; }
    public String getComprovante() { return comprovante; }
    public void setComprovante(String comprovante) { this.comprovante = comprovante; }
}
