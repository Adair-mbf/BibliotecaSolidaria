package br.com.bibliotecasolidaria.model;

public class ExemplarFisico extends ItemAcervo {
    private String codigoTombo;
    private EstadoConservacao estado;
    private String localizacao;

    public ExemplarFisico() { super(); }

    public ExemplarFisico(Long id, Long livroId, StatusItem status, String codigoTombo, EstadoConservacao estado, String localizacao) {
        super(id, livroId, status);
        this.codigoTombo = codigoTombo;
        this.estado = estado;
        this.localizacao = localizacao;
    }

    public String getCodigoTombo() { return codigoTombo; }
    public void setCodigoTombo(String codigoTombo) { this.codigoTombo = codigoTombo; }
    public EstadoConservacao getEstado() { return estado; }
    public void setEstado(EstadoConservacao estado) { this.estado = estado; }
    public String getLocalizacao() { return localizacao; }
    public void setLocalizacao(String localizacao) { this.localizacao = localizacao; }
}
