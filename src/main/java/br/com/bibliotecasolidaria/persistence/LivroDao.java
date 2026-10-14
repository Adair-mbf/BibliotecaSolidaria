package br.com.bibliotecasolidaria.persistence;

import br.com.bibliotecasolidaria.model.Domain.CategoriaLivro;
import br.com.bibliotecasolidaria.model.Domain.Livro;

public final class LivroDao extends JdbcCrudDao<Livro> {
    public LivroDao(SqlConnectionProvider connections) {
        super(
                connections,
                "livros",
                "INSERT INTO livros (titulo, autor, editora, isbn, categoria, ano_publicacao) "
                        + "VALUES (?, ?, ?, ?, ?, ?) RETURNING id",
                "UPDATE livros SET titulo = ?, autor = ?, editora = ?, isbn = ?, categoria = ?, "
                        + "ano_publicacao = ? WHERE id = ?",
                resultSet -> new Livro(
                        resultSet.getLong("id"),
                        resultSet.getString("titulo"),
                        resultSet.getString("autor"),
                        resultSet.getString("editora"),
                        resultSet.getString("isbn"),
                        CategoriaLivro.valueOf(resultSet.getString("categoria")),
                        resultSet.getInt("ano_publicacao")),
                livro -> new Object[]{
                        livro.titulo(), livro.autor(), livro.editora(), livro.isbn(),
                        livro.categoria().name(), livro.anoPublicacao()
                },
                livro -> requiredId(livro.id()),
                (livro, id) -> new Livro(
                        id, livro.titulo(), livro.autor(), livro.editora(), livro.isbn(),
                        livro.categoria(), livro.anoPublicacao()));
    }
}
