package br.com.bibliotecasolidaria.model;

public class Livro {
    private Long id;
    private String titulo;
    private String autor;
    private String editora;
    private String isbn;
    private CategoriaLivro categoria;
    private int anoPublicacao;

    public Livro() {}

    public Livro(Long id, String titulo, String autor, String editora, String isbn, CategoriaLivro categoria, int anoPublicacao) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.editora = editora;
        this.isbn = isbn;
        this.categoria = categoria;
        this.anoPublicacao = anoPublicacao;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    public String getEditora() { return editora; }
    public void setEditora(String editora) { this.editora = editora; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public CategoriaLivro getCategoria() { return categoria; }
    public void setCategoria(CategoriaLivro categoria) { this.categoria = categoria; }
    public int getAnoPublicacao() { return anoPublicacao; }
    public void setAnoPublicacao(int anoPublicacao) { this.anoPublicacao = anoPublicacao; }
}
