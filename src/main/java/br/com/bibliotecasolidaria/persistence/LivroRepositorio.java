package br.com.bibliotecasolidaria.persistence;

import br.com.bibliotecasolidaria.model.Livro;
import br.com.bibliotecasolidaria.model.enums.CategoriaLivro;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public final class LivroRepositorio extends RepositorioJdbcLong<Livro> {
    public LivroRepositorio(SqlConnectionProvider conexoes) { super(conexoes); }

    @Override protected String tabela() { return "livros"; }

    @Override protected String sqlInsert() {
        return "INSERT INTO livros (titulo, autor, editora, isbn, categoria, ano_publicacao) VALUES (?, ?, ?, ?, ?, ?)";
    }

    @Override protected String sqlUpdate() {
        return "UPDATE livros SET titulo = ?, autor = ?, editora = ?, isbn = ?, categoria = ?, "
                + "ano_publicacao = ? WHERE id = ?";
    }

    @Override protected Long getId(Livro l) { return l.getId(); }

    @Override protected void setId(Livro l, Long id) { l.setId(id); }

    @Override
    protected Object[] valores(Livro l) {
        return new Object[]{l.getTitulo(), l.getAutor(), l.getEditora(), l.getIsbn(), nome(l.getCategoria()),
                l.getAnoPublicacao()};
    }

    @Override
    protected Livro mapear(ResultSet rs) throws SQLException {
        return new Livro(rs.getLong("id"), rs.getString("titulo"), rs.getString("autor"), rs.getString("editora"),
                rs.getString("isbn"), CategoriaLivro.valueOf(rs.getString("categoria")), rs.getInt("ano_publicacao"));
    }

    public List<Livro> buscarPorTitulo(String titulo) {
        return consultar("SELECT * FROM livros WHERE LOWER(titulo) LIKE LOWER(?) ORDER BY titulo, id",
                "%" + titulo + "%");
    }

    public List<Livro> buscarPorCategoria(CategoriaLivro categoria) {
        return consultar("SELECT * FROM livros WHERE categoria = ? ORDER BY titulo, id", categoria.name());
    }

    public Optional<Livro> buscarPorIsbn(String isbn) {
        return consultarUm("SELECT * FROM livros WHERE isbn = ? ORDER BY id", isbn);
    }
}
