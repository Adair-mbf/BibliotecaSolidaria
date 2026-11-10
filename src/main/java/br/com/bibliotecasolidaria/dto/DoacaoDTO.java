package br.com.bibliotecasolidaria.dto;

import java.util.Map;

public class DoacaoDTO {
    private Long idDoador;
    private String tipo;
    private Map<String, Object> dadosItem;

    public DoacaoDTO() {}

    public DoacaoDTO(Long idDoador, String tipo, Map<String, Object> dadosItem) {
        this.idDoador = idDoador;
        this.tipo = tipo;
        this.dadosItem = dadosItem;
    }

    public Long getIdDoador() { return idDoador; }
    public void setIdDoador(Long idDoador) { this.idDoador = idDoador; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Map<String, Object> getDadosItem() { return dadosItem; }
    public void setDadosItem(Map<String, Object> dadosItem) { this.dadosItem = dadosItem; }
}
