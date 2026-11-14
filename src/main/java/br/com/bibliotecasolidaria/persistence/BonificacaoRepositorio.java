package br.com.bibliotecasolidaria.persistence;

import br.com.bibliotecasolidaria.model.Bonificacao;
import br.com.bibliotecasolidaria.model.enums.TipoBonificacao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public final class BonificacaoRepositorio extends RepositorioJdbcLong<Bonificacao> {
    public BonificacaoRepositorio(SqlConnectionProvider conexoes) { super(conexoes); }

    @Override protected String tabela() { return "bonificacoes"; }

    @Override protected String sqlInsert() {
        return "INSERT INTO bonificacoes (doador_id, tipo, descricao, data_concessao) VALUES (?, ?, ?, ?)";
    }

    @Override protected String sqlUpdate() {
        return "UPDATE bonificacoes SET doador_id = ?, tipo = ?, descricao = ?, data_concessao = ? WHERE id = ?";
    }

    @Override protected Long getId(Bonificacao b) { return b.getId(); }

    @Override protected void setId(Bonificacao b, Long id) { b.setId(id); }

    @Override
    protected Object[] valores(Bonificacao b) {
        return new Object[]{b.getDoadorId(), b.getTipo().name(), b.getDescricao(), b.getDataConcessao()};
    }

    @Override
    protected Bonificacao mapear(ResultSet rs) throws SQLException {
        return new Bonificacao(rs.getLong("id"), rs.getObject("doador_id", Long.class),
                TipoBonificacao.valueOf(rs.getString("tipo")), rs.getString("descricao"),
                rs.getObject("data_concessao", LocalDate.class));
    }

    public List<Bonificacao> listarPorDoador(long idDoador) {
        return consultar("SELECT * FROM bonificacoes WHERE doador_id = ? ORDER BY id", idDoador);
    }
}
