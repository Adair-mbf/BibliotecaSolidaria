package br.com.bibliotecasolidaria.model;

import br.com.bibliotecasolidaria.model.enums.StatusItem;
import br.com.bibliotecasolidaria.model.enums.TipoItemAcervo;

/** Cópia digital: a licença permite acessos simultâneos, logo está sempre disponível. */
public class Ebook extends ItemAcervo {
    private String arquivoUrl;
    private String formato;

    public Ebook(Long id, Long livroId, StatusItem status, String arquivoUrl, String formato) {
        super(id, livroId, status);
        this.arquivoUrl = arquivoUrl;
        this.formato = formato;
    }

    public String getArquivoUrl() { return arquivoUrl; }
    public void setArquivoUrl(String arquivoUrl) { this.arquivoUrl = arquivoUrl; }
    public String getFormato() { return formato; }
    public void setFormato(String formato) { this.formato = formato; }

    @Override
    public TipoItemAcervo getTipo() { return TipoItemAcervo.EBOOK; }

    @Override
    public boolean estaDisponivel() { return true; }

    @Override
    public void marcarEmprestado() { /* licença digital: o status não muda */ }

    @Override
    public void marcarDisponivel() { /* licença digital: o status não muda */ }

    @Override
    public void marcarReservado() { /* licença digital: não há fila */ }

    @Override
    public String toString() {
        return "Ebook{" + "id=" + getId() + ", " + "livroId=" + getLivroId() + ", " + "status=" + getStatus() + ", " + "arquivoUrl='" + getArquivoUrl() + "'" + ", " + "formato='" + getFormato() + "'" + "}";
    }
}
