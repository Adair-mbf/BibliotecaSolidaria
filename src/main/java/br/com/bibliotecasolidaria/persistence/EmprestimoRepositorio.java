package br.com.bibliotecasolidaria.persistence;

import br.com.bibliotecasolidaria.model.Emprestimo;
import br.com.bibliotecasolidaria.model.enums.StatusEmprestimo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public final class EmprestimoRepositorio extends RepositorioJdbcLong<Emprestimo> {
    private static final String EM_ABERTO = "status IN ('ATIVO', 'ATRASADO')";

    public EmprestimoRepositorio(SqlConnectionProvider conexoes) { super(conexoes); }

    @Override protected String tabela() { return "emprestimos"; }

    @Override protected String sqlInsert() {
        return "INSERT INTO emprestimos (usuario_id, item_acervo_id, data_emprestimo, data_prevista_devolucao, "
                + "data_devolucao, renovacoes, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
    }

    @Override protected String sqlUpdate() {
        return "UPDATE emprestimos SET usuario_id = ?, item_acervo_id = ?, data_emprestimo = ?, "
                + "data_prevista_devolucao = ?, data_devolucao = ?, renovacoes = ?, status = ? WHERE id = ?";
    }

    @Override protected Long getId(Emprestimo e) { return e.getId(); }

    @Override protected void setId(Emprestimo e, Long id) { e.setId(id); }

    @Override
    protected Object[] valores(Emprestimo e) {
        return new Object[]{e.getUsuarioId(), e.getItemAcervoId(), e.getDataEmprestimo(),
                e.getDataPrevistaDevolucao(), e.getDataDevolucao(), e.getRenovacoes(), e.getStatus().name()};
    }

    @Override
    protected Emprestimo mapear(ResultSet rs) throws SQLException {
        return new Emprestimo(rs.getLong("id"), rs.getObject("usuario_id", Long.class),
                rs.getObject("item_acervo_id", Long.class), rs.getObject("data_emprestimo", LocalDate.class),
                rs.getObject("data_prevista_devolucao", LocalDate.class),
                rs.getObject("data_devolucao", LocalDate.class), rs.getInt("renovacoes"),
                StatusEmprestimo.valueOf(rs.getString("status")));
    }

    public List<Emprestimo> listarAtrasados() {
        return listarAtrasados(LocalDate.now());
    }

    /** Empréstimos em aberto cujo prazo venceu antes da data de referência. */
    public List<Emprestimo> listarAtrasados(LocalDate referencia) {
        return consultar("SELECT * FROM emprestimos WHERE " + EM_ABERTO
                + " AND data_prevista_devolucao < ? ORDER BY data_prevista_devolucao, id", referencia);
    }

    public List<Emprestimo> listarPorUsuario(long usuarioId) {
        return consultar("SELECT * FROM emprestimos WHERE usuario_id = ? ORDER BY data_emprestimo DESC, id DESC",
                usuarioId);
    }

    public List<Emprestimo> listarEmAbertoPorItem(long itemId) {
        return consultar("SELECT * FROM emprestimos WHERE item_acervo_id = ? AND " + EM_ABERTO + " ORDER BY id",
                itemId);
    }

    /** Empréstimos em aberto com prazo entre as duas datas (inclusive). */
    public List<Emprestimo> listarEmAbertoComVencimentoEntre(LocalDate inicio, LocalDate fim) {
        return consultar("SELECT * FROM emprestimos WHERE " + EM_ABERTO
                + " AND data_prevista_devolucao BETWEEN ? AND ? ORDER BY data_prevista_devolucao, id", inicio, fim);
    }

    public List<Emprestimo> listarPorPeriodo(LocalDate inicio, LocalDate fim) {
        return consultar("SELECT * FROM emprestimos WHERE data_emprestimo BETWEEN ? AND ? "
                + "ORDER BY data_emprestimo, id", inicio, fim);
    }
}
