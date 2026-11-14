package br.com.bibliotecasolidaria.model;

import br.com.bibliotecasolidaria.model.enums.TipoBonificacao;
import java.time.LocalDate;

/** Reconhecimento concedido a um doador. */
public class Bonificacao {
    private Long id;
    private Long doadorId;
    private TipoBonificacao tipo;
    private String descricao;
    private LocalDate dataConcessao;

    public Bonificacao(Long id, Long doadorId, TipoBonificacao tipo, String descricao, LocalDate dataConcessao) {
        this.id = id;
        this.doadorId = doadorId;
        this.tipo = tipo;
        this.descricao = descricao;
        this.dataConcessao = dataConcessao;
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

    @Override
    public String toString() {
        return "Bonificacao{" + "id=" + getId() + ", " + "doadorId=" + getDoadorId() + ", " + "tipo=" + getTipo() + ", " + "descricao='" + getDescricao() + "'" + ", " + "dataConcessao=" + getDataConcessao() + "}";
    }
}
