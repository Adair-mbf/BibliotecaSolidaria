package br.com.bibliotecasolidaria.persistence;

import br.com.bibliotecasolidaria.model.Domain.Notificacao;
import br.com.bibliotecasolidaria.model.Domain.TipoEvento;

public final class NotificacaoDao extends JdbcCrudDao<Notificacao> {
    public NotificacaoDao(SqlConnectionProvider connections) {
        super(
                connections,
                "notificacoes",
                "INSERT INTO notificacoes (usuario_id, tipo, mensagem, data_envio, lida) "
                        + "VALUES (?, ?, ?, ?, ?) RETURNING id",
                "UPDATE notificacoes SET usuario_id = ?, tipo = ?, mensagem = ?, data_envio = ?, lida = ? "
                        + "WHERE id = ?",
                resultSet -> new Notificacao(
                        resultSet.getLong("id"),
                        resultSet.getObject("usuario_id", Long.class),
                        TipoEvento.valueOf(resultSet.getString("tipo")),
                        resultSet.getString("mensagem"),
                        resultSet.getObject("data_envio", java.time.LocalDateTime.class),
                        resultSet.getBoolean("lida")),
                notificacao -> new Object[]{
                        notificacao.usuarioId(), notificacao.tipo().name(), notificacao.mensagem(),
                        notificacao.dataEnvio(), notificacao.lida()
                },
                notificacao -> requiredId(notificacao.id()),
                (notificacao, id) -> new Notificacao(
                        id, notificacao.usuarioId(), notificacao.tipo(), notificacao.mensagem(),
                        notificacao.dataEnvio(), notificacao.lida()));
    }
}
