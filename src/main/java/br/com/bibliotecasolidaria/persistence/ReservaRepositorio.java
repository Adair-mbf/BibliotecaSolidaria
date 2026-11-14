package br.com.bibliotecasolidaria.persistence;

import br.com.bibliotecasolidaria.model.Reserva;
import br.com.bibliotecasolidaria.model.enums.StatusReserva;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public final class ReservaRepositorio extends RepositorioJdbcLong<Reserva> {
    public ReservaRepositorio(SqlConnectionProvider conexoes) { super(conexoes); }

    @Override protected String tabela() { return "reservas"; }

    @Override protected String sqlInsert() {
        return "INSERT INTO reservas (usuario_id, livro_id, data_reserva, status) VALUES (?, ?, ?, ?)";
    }

    @Override protected String sqlUpdate() {
        return "UPDATE reservas SET usuario_id = ?, livro_id = ?, data_reserva = ?, status = ? WHERE id = ?";
    }

    @Override protected Long getId(Reserva r) { return r.getId(); }

    @Override protected void setId(Reserva r, Long id) { r.setId(id); }

    @Override
    protected Object[] valores(Reserva r) {
        return new Object[]{r.getUsuarioId(), r.getLivroId(), r.getDataReserva(), r.getStatus().name()};
    }

    @Override
    protected Reserva mapear(ResultSet rs) throws SQLException {
        return new Reserva(rs.getLong("id"), rs.getObject("usuario_id", Long.class),
                rs.getObject("livro_id", Long.class), rs.getObject("data_reserva", LocalDate.class),
                StatusReserva.valueOf(rs.getString("status")));
    }

    /** Primeira reserva ativa da fila (mais antiga). */
    public Optional<Reserva> proximaDaFila(long idLivro) {
        return consultarUm("SELECT * FROM reservas WHERE livro_id = ? AND status = 'ATIVA' "
                + "ORDER BY data_reserva, id LIMIT 1", idLivro);
    }

    public List<Reserva> listarAtivasPorLivro(long idLivro) {
        return consultar("SELECT * FROM reservas WHERE livro_id = ? AND status = 'ATIVA' ORDER BY data_reserva, id",
                idLivro);
    }

    public List<Reserva> listarPorUsuario(long idUsuario) {
        return consultar("SELECT * FROM reservas WHERE usuario_id = ? ORDER BY id", idUsuario);
    }

    public Optional<Reserva> buscarAtiva(long idUsuario, long idLivro) {
        return consultarUm("SELECT * FROM reservas WHERE usuario_id = ? AND livro_id = ? AND status = 'ATIVA'",
                idUsuario, idLivro);
    }
}
