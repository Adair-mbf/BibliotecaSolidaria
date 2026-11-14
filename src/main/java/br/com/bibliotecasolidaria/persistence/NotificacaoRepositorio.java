package br.com.bibliotecasolidaria.persistence;

import br.com.bibliotecasolidaria.model.Notificacao;
import br.com.bibliotecasolidaria.model.enums.TipoEvento;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public final class NotificacaoRepositorio extends RepositorioJdbcLong<Notificacao> {
    public NotificacaoRepositorio(SqlConnectionProvider conexoes) { super(conexoes); }

    @Override protected String tabela() { return "notificacoes"; }

    @Override protected String sqlInsert() {
        return "INSERT INTO notificacoes (usuario_id, tipo, mensagem, data_envio, lida) VALUES (?, ?, ?, ?, ?)";
    }

    @Override protected String sqlUpdate() {
        return "UPDATE notificacoes SET usuario_id = ?, tipo = ?, mensagem = ?, data_envio = ?, lida = ? WHERE id = ?";
    }

    @Override protected Long getId(Notificacao n) { return n.getId(); }

    @Override protected void setId(Notificacao n, Long id) { n.setId(id); }

    @Override
    protected Object[] valores(Notificacao n) {
        return new Object[]{n.getUsuarioId(), n.getTipo().name(), n.getMensagem(), n.getDataEnvio(), n.isLida()};
    }

    @Override
    protected Notificacao mapear(ResultSet rs) throws SQLException {
        return new Notificacao(rs.getLong("id"), rs.getObject("usuario_id", Long.class),
                TipoEvento.valueOf(rs.getString("tipo")), rs.getString("mensagem"),
                rs.getObject("data_envio", LocalDateTime.class), rs.getBoolean("lida"));
    }

    public List<Notificacao> listarPorUsuario(long idUsuario) {
        return consultar("SELECT * FROM notificacoes WHERE usuario_id = ? ORDER BY data_envio DESC, id DESC",
                idUsuario);
    }

    public List<Notificacao> listarNaoLidas(long idUsuario) {
        return consultar("SELECT * FROM notificacoes WHERE usuario_id = ? AND lida = FALSE "
                + "ORDER BY data_envio DESC, id DESC", idUsuario);
    }

    /** Indica se já existe notificação idêntica enviada ao usuário no dia informado. */
    public boolean existeNoDia(long idUsuario, TipoEvento tipo, String mensagem, LocalDate dia) {
        return existe("SELECT 1 FROM notificacoes WHERE usuario_id = ? AND tipo = ? AND mensagem = ? "
                + "AND data_envio >= ? AND data_envio < ?", idUsuario, tipo.name(), mensagem,
                dia.atStartOfDay(), dia.plusDays(1).atStartOfDay());
    }
}
