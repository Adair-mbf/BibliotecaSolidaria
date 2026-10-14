package br.com.bibliotecasolidaria.persistence;

import br.com.bibliotecasolidaria.model.Domain.Reserva;
import br.com.bibliotecasolidaria.model.Domain.StatusReserva;

public final class ReservaDao extends JdbcCrudDao<Reserva> {
    public ReservaDao(SqlConnectionProvider connections) {
        super(
                connections,
                "reservas",
                "INSERT INTO reservas (usuario_id, livro_id, data_reserva, status) "
                        + "VALUES (?, ?, ?, ?) RETURNING id",
                "UPDATE reservas SET usuario_id = ?, livro_id = ?, data_reserva = ?, status = ? WHERE id = ?",
                resultSet -> new Reserva(
                        resultSet.getLong("id"),
                        resultSet.getObject("usuario_id", Long.class),
                        resultSet.getObject("livro_id", Long.class),
                        resultSet.getObject("data_reserva", java.time.LocalDate.class),
                        StatusReserva.valueOf(resultSet.getString("status"))),
                reserva -> new Object[]{
                        reserva.usuarioId(), reserva.livroId(), reserva.dataReserva(), reserva.status().name()
                },
                reserva -> requiredId(reserva.id()),
                (reserva, id) -> new Reserva(
                        id, reserva.usuarioId(), reserva.livroId(), reserva.dataReserva(), reserva.status()));
    }
}
