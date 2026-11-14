package br.com.bibliotecasolidaria.persistence;

import br.com.bibliotecasolidaria.model.Multa;
import br.com.bibliotecasolidaria.model.enums.StatusMulta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public final class MultaRepositorio extends RepositorioJdbcLong<Multa> {
    public MultaRepositorio(SqlConnectionProvider conexoes) { super(conexoes); }

    @Override protected String tabela() { return "multas"; }

    @Override protected String sqlInsert() {
        return "INSERT INTO multas (emprestimo_id, valor, data_geracao, data_pagamento, status) VALUES (?, ?, ?, ?, ?)";
    }

    @Override protected String sqlUpdate() {
        return "UPDATE multas SET emprestimo_id = ?, valor = ?, data_geracao = ?, data_pagamento = ?, "
                + "status = ? WHERE id = ?";
    }

    @Override protected Long getId(Multa m) { return m.getId(); }

    @Override protected void setId(Multa m, Long id) { m.setId(id); }

    @Override
    protected Object[] valores(Multa m) {
        return new Object[]{m.getEmprestimoId(), m.getValor(), m.getDataGeracao(), m.getDataPagamento(),
                m.getStatus().name()};
    }

    @Override
    protected Multa mapear(ResultSet rs) throws SQLException {
        return new Multa(rs.getLong("id"), rs.getObject("emprestimo_id", Long.class), rs.getBigDecimal("valor"),
                rs.getObject("data_geracao", LocalDate.class), rs.getObject("data_pagamento", LocalDate.class),
                StatusMulta.valueOf(rs.getString("status")));
    }

    public List<Multa> listarPendentes(long idUsuario) {
        return consultar("SELECT m.* FROM multas m JOIN emprestimos e ON e.id = m.emprestimo_id "
                + "WHERE e.usuario_id = ? AND m.status = 'PENDENTE' ORDER BY m.id", idUsuario);
    }

    public Optional<Multa> buscarPorEmprestimo(long emprestimoId) {
        return consultarUm("SELECT * FROM multas WHERE emprestimo_id = ?", emprestimoId);
    }

    public List<Multa> listarGeradasNoPeriodo(LocalDate inicio, LocalDate fim) {
        return consultar("SELECT * FROM multas WHERE data_geracao BETWEEN ? AND ? ORDER BY data_geracao, id",
                inicio, fim);
    }
}
