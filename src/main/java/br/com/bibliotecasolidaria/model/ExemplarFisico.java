package br.com.bibliotecasolidaria.model;

import br.com.bibliotecasolidaria.model.enums.EstadoConservacao;
import br.com.bibliotecasolidaria.model.enums.StatusItem;
import br.com.bibliotecasolidaria.model.enums.TipoItemAcervo;

/** Cópia física de um livro, identificada pelo código de tombo. */
public class ExemplarFisico extends ItemAcervo {
    private String codigoTombo;
    private EstadoConservacao estado;
    private String localizacao;

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

    @Override
    public TipoItemAcervo getTipo() { return TipoItemAcervo.EXEMPLAR_FISICO; }

    @Override
    public String toString() {
        return "ExemplarFisico{" + "id=" + getId() + ", " + "livroId=" + getLivroId() + ", " + "status=" + getStatus() + ", " + "codigoTombo='" + getCodigoTombo() + "'" + ", " + "estado=" + getEstado() + ", " + "localizacao='" + getLocalizacao() + "'" + "}";
    }
}
