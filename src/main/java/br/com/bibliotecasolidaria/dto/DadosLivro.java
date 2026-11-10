package br.com.bibliotecasolidaria.dto;

import java.util.List;

public class DadosLivro {
    private String titulo;
    private List<String> autores;
    private String isbn;
    private String editora;
    private int anoPublicacao;
    private String capaUrl;

    public DadosLivro() {}

    public DadosLivro(String titulo, List<String> autores, String isbn, String editora, int anoPublicacao, String capaUrl) {
        this.titulo = titulo;
        this.autores = autores;
        this.isbn = isbn;
        this.editora = editora;
        this.anoPublicacao = anoPublicacao;
        this.capaUrl = capaUrl;
    }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public List<String> getAutores() { return autores; }
    public void setAutores(List<String> autores) { this.autores = autores; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getEditora() { return editora; }
    public void setEditora(String editora) { this.editora = editora; }
    public int getAnoPublicacao() { return anoPublicacao; }
    public void setAnoPublicacao(int anoPublicacao) { this.anoPublicacao = anoPublicacao; }
    public String getCapaUrl() { return capaUrl; }
    public void setCapaUrl(String capaUrl) { this.capaUrl = capaUrl; }
}
