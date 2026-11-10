package br.com.bibliotecasolidaria.model;

public class Ebook extends ItemAcervo {
    private String arquivoUrl;
    private String formato;

    public Ebook() { super(); }

    public Ebook(Long id, Long livroId, StatusItem status, String arquivoUrl, String formato) {
        super(id, livroId, status);
        this.arquivoUrl = arquivoUrl;
        this.formato = formato;
    }

    @Override
    public boolean estaDisponivel() {
        return true;
    }

    public String getArquivoUrl() { return arquivoUrl; }
    public void setArquivoUrl(String arquivoUrl) { this.arquivoUrl = arquivoUrl; }
    public String getFormato() { return formato; }
    public void setFormato(String formato) { this.formato = formato; }
}
