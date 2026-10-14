package br.com.bibliotecasolidaria.persistence;

import br.com.bibliotecasolidaria.model.Domain.Emprestimo;
import br.com.bibliotecasolidaria.model.Domain.StatusEmprestimo;

import java.sql.SQLException;
import java.util.List;

public final class EmprestimoDao extends JdbcCrudDao<Emprestimo> {
    public EmprestimoDao(SqlConnectionProvider connections) {
        super(
                connections,
                "emprestimos",
                "INSERT INTO emprestimos (usuario_id, item_acervo_id, data_emprestimo, "
                        + "data_prevista_devolucao, data_devolucao, renovacoes, status) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id",
                "UPDATE emprestimos SET usuario_id = ?, item_acervo_id = ?, data_emprestimo = ?, "
                        + "data_prevista_devolucao = ?, data_devolucao = ?, renovacoes = ?, status = ? "
                        + "WHERE id = ?",
                resultSet -> new Emprestimo(
                        resultSet.getLong("id"),
                        resultSet.getObject("usuario_id", Long.class),
                        resultSet.getObject("item_acervo_id", Long.class),
                        resultSet.getObject("data_emprestimo", java.time.LocalDate.class),
                        resultSet.getObject("data_prevista_devolucao", java.time.LocalDate.class),
                        resultSet.getObject("data_devolucao", java.time.LocalDate.class),
                        resultSet.getInt("renovacoes"),
                        StatusEmprestimo.valueOf(resultSet.getString("status"))),
                emprestimo -> new Object[]{
                        emprestimo.usuarioId(), emprestimo.itemAcervoId(), emprestimo.dataEmprestimo(),
                        emprestimo.dataPrevistaDevolucao(), emprestimo.dataDevolucao(),
                        emprestimo.renovacoes(), emprestimo.status().name()
                },
                emprestimo -> requiredId(emprestimo.id()),
                (emprestimo, id) -> new Emprestimo(
                        id, emprestimo.usuarioId(), emprestimo.itemAcervoId(), emprestimo.dataEmprestimo(),
                        emprestimo.dataPrevistaDevolucao(), emprestimo.dataDevolucao(),
                        emprestimo.renovacoes(), emprestimo.status()));
    }

    public List<Emprestimo> listarAtrasados() throws SQLException {
        return query("SELECT * FROM emprestimos "
                + "WHERE status IN ('ATIVO', 'ATRASADO') "
                + "AND data_prevista_devolucao < CURRENT_DATE ORDER BY data_prevista_devolucao");
    }

    public List<Emprestimo> listarPorUsuario(long usuarioId) throws SQLException {
        return query("SELECT * FROM emprestimos WHERE usuario_id = ? ORDER BY data_emprestimo DESC", usuarioId);
    }
}
