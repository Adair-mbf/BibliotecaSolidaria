package br.com.bibliotecasolidaria.model;

import java.time.LocalDate;

public class DoacaoLivroFisico extends Doacao {
    private String titulo;
    private String autor;
    private String isbn;
    private CategoriaLivro categoria;
    private EstadoConservacao estado;
    private int quantidade;

    public DoacaoLivroFisico() { super(); }

    public DoacaoLivroFisico(Long id, Long doadorId, LocalDate dataRegistro, StatusDoacao status,
                             String titulo, String autor, String isbn, CategoriaLivro categoria,
                             EstadoConservacao estado, int quantidade) {
        super(id, doadorId, dataRegistro, status);
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.categoria = categoria;
        this.estado = estado;
        this.quantidade = quantidade;
    }

    @Override
    public boolean avaliar() {
        return estado != EstadoConservacao.RUIM && quantidade > 0;
    }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public CategoriaLivro getCategoria() { return categoria; }
    public void setCategoria(CategoriaLivro categoria) { this.categoria = categoria; }
    public EstadoConservacao getEstado() { return estado; }
    public void setEstado(EstadoConservacao estado) { this.estado = estado; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
}
