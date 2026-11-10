package br.com.bibliotecasolidaria.controller;

import br.com.bibliotecasolidaria.model.Domain.CategoriaLivro;
import br.com.bibliotecasolidaria.model.Domain.Livro;
import br.com.bibliotecasolidaria.persistence.LivroDao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/** Coordena os casos de uso de livros entre a view e a persistência. */
public final class LivroController {
    private final LivroDao livros;

    public LivroController(LivroDao livros) { this.livros = livros; }

    public Livro criar(String titulo, String autor, String editora, String isbn,
                       CategoriaLivro categoria, int anoPublicacao) throws SQLException {
        validar(titulo, autor, categoria, anoPublicacao);
        return livros.save(new Livro(null, titulo.trim(), autor.trim(), optional(editora),
                optional(isbn), categoria, anoPublicacao));
    }

    public List<Livro> listar() throws SQLException { return livros.findAll(); }

    public Optional<Livro> buscar(long id) throws SQLException { return livros.findById(id); }

    public boolean atualizar(long id, String titulo, String autor, String editora, String isbn,
                             CategoriaLivro categoria, int anoPublicacao) throws SQLException {
        validar(titulo, autor, categoria, anoPublicacao);
        return livros.update(new Livro(id, titulo.trim(), autor.trim(), optional(editora),
                optional(isbn), categoria, anoPublicacao));
    }

    public boolean remover(long id) throws SQLException { return livros.deleteById(id); }

    private static void validar(String titulo, String autor, CategoriaLivro categoria, int ano) {
        if (titulo == null || titulo.isBlank()) throw new IllegalArgumentException("O título é obrigatório.");
        if (autor == null || autor.isBlank()) throw new IllegalArgumentException("O autor é obrigatório.");
        if (categoria == null) throw new IllegalArgumentException("A categoria é obrigatória.");
        if (ano < 0 || ano > java.time.Year.now().getValue()) {
            throw new IllegalArgumentException("O ano de publicação é inválido.");
        }
    }

    private static String optional(String value) { return value == null || value.isBlank() ? null : value.trim(); }
}
