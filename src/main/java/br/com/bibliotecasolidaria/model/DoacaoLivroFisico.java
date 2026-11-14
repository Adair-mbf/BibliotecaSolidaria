package br.com.bibliotecasolidaria.model;

import br.com.bibliotecasolidaria.model.enums.CategoriaLivro;
import br.com.bibliotecasolidaria.model.enums.EstadoConservacao;
import br.com.bibliotecasolidaria.model.enums.StatusDoacao;
import br.com.bibliotecasolidaria.model.enums.TipoDoacao;
import java.time.LocalDate;

/** Doação de livros físicos. */
public class DoacaoLivroFisico extends Doacao {
    private String titulo;
    private String autor;
    private String isbn;
    private CategoriaLivro categoria;
    private EstadoConservacao estado;
    private int quantidade;

    public DoacaoLivroFisico(Long id, Long doadorId, LocalDate dataRegistro, StatusDoacao status, String titulo, String autor, String isbn, CategoriaLivro categoria, EstadoConservacao estado, int quantidade) {
        super(id, doadorId, dataRegistro, status);
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.categoria = categoria;
        this.estado = estado;
        this.quantidade = quantidade;
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

    /** Estado de conservação mínimo aceito pela biblioteca. */
    public static final EstadoConservacao ESTADO_MINIMO = EstadoConservacao.ACEITAVEL;

    @Override
    public boolean avaliar() {
        return preenchido(titulo) && preenchido(autor) && preenchido(isbn) && categoria != null
                && estado != null && estado.ordinal() <= ESTADO_MINIMO.ordinal() && quantidade > 0;
    }

    @Override
    public TipoDoacao getTipo() { return TipoDoacao.LIVRO_FISICO; }

    @Override
    public String toString() {
        return "DoacaoLivroFisico{" + "id=" + getId() + ", " + "doadorId=" + getDoadorId() + ", " + "dataRegistro=" + getDataRegistro() + ", " + "status=" + getStatus() + ", " + "titulo='" + getTitulo() + "'" + ", " + "autor='" + getAutor() + "'" + ", " + "isbn='" + getIsbn() + "'" + ", " + "categoria=" + getCategoria() + ", " + "estado=" + getEstado() + ", " + "quantidade=" + getQuantidade() + "}";
    }
}
