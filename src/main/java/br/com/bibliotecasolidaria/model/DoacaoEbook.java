package br.com.bibliotecasolidaria.model;

import java.time.LocalDate;

public class DoacaoEbook extends Doacao {
    private String titulo;
    private String autor;
    private String arquivoUrl;
    private String formato;

    public DoacaoEbook() { super(); }

    public DoacaoEbook(Long id, Long doadorId, LocalDate dataRegistro, StatusDoacao status,
                       String titulo, String autor, String arquivoUrl, String formato) {
        super(id, doadorId, dataRegistro, status);
        this.titulo = titulo;
        this.autor = autor;
        this.arquivoUrl = arquivoUrl;
        this.formato = formato;
    }

    @Override
    public boolean avaliar() {
        return arquivoUrl != null && !arquivoUrl.isBlank() && formato != null;
    }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    public String getArquivoUrl() { return arquivoUrl; }
    public void setArquivoUrl(String arquivoUrl) { this.arquivoUrl = arquivoUrl; }
    public String getFormato() { return formato; }
    public void setFormato(String formato) { this.formato = formato; }
}
