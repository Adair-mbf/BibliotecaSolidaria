package br.com.bibliotecasolidaria.model;

import br.com.bibliotecasolidaria.model.enums.StatusDoacao;
import br.com.bibliotecasolidaria.model.enums.TipoDoacao;
import java.time.LocalDate;
import java.util.Set;

/** Doação de livro digital. */
public class DoacaoEbook extends Doacao {
    private String titulo;
    private String autor;
    private String arquivoUrl;
    private String formato;

    public DoacaoEbook(Long id, Long doadorId, LocalDate dataRegistro, StatusDoacao status, String titulo, String autor, String arquivoUrl, String formato) {
        super(id, doadorId, dataRegistro, status);
        this.titulo = titulo;
        this.autor = autor;
        this.arquivoUrl = arquivoUrl;
        this.formato = formato;
    }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    public String getArquivoUrl() { return arquivoUrl; }
    public void setArquivoUrl(String arquivoUrl) { this.arquivoUrl = arquivoUrl; }
    public String getFormato() { return formato; }
    public void setFormato(String formato) { this.formato = formato; }

    public static final Set<String> FORMATOS_ACEITOS = Set.of("PDF", "EPUB", "MOBI", "AZW3");

    @Override
    public boolean avaliar() {
        return preenchido(titulo) && preenchido(autor) && preenchido(arquivoUrl) && preenchido(formato)
                && FORMATOS_ACEITOS.contains(formato.trim().toUpperCase());
    }

    @Override
    public TipoDoacao getTipo() { return TipoDoacao.EBOOK; }

    @Override
    public String toString() {
        return "DoacaoEbook{" + "id=" + getId() + ", " + "doadorId=" + getDoadorId() + ", " + "dataRegistro=" + getDataRegistro() + ", " + "status=" + getStatus() + ", " + "titulo='" + getTitulo() + "'" + ", " + "autor='" + getAutor() + "'" + ", " + "arquivoUrl='" + getArquivoUrl() + "'" + ", " + "formato='" + getFormato() + "'" + "}";
    }
}
