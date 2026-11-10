package br.com.bibliotecasolidaria.model;

import java.time.LocalDate;

public class Bonificacao {
    private Long id;
    private Long doadorId;
    private TipoBonificacao tipo;
    private String descricao;
    private LocalDate dataConcessao;

    public Bonificacao() {}

    public Bonificacao(Long id, Long doadorId, TipoBonificacao tipo, String descricao, LocalDate dataConcessao) {
        this.id = id;
        this.doadorId = doadorId;
        this.tipo = tipo;
        this.descricao = descricao;
        this.dataConcessao = dataConcessao != null ? dataConcessao : LocalDate.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getDoadorId() { return doadorId; }
    public void setDoadorId(Long doadorId) { this.doadorId = doadorId; }
    public TipoBonificacao getTipo() { return tipo; }
    public void setTipo(TipoBonificacao tipo) { this.tipo = tipo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public LocalDate getDataConcessao() { return dataConcessao; }
    public void setDataConcessao(LocalDate dataConcessao) { this.dataConcessao = dataConcessao; }
}
