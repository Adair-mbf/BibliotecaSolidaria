package br.com.bibliotecasolidaria.persistence;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToLongFunction;

public abstract class JdbcCrudDao<T> implements Dao<T> {
    @FunctionalInterface
    protected interface RowMapper<T> {
        T map(ResultSet resultSet) throws SQLException;
    }

    private final SqlConnectionProvider connections;
    private final String table;
    private final String insertSql;
    private final String updateSql;
    private final RowMapper<T> rowMapper;
    private final Function<T, Object[]> values;
    private final ToLongFunction<T> id;
    private final BiFunction<T, Long, T> withId;

    protected JdbcCrudDao(
            SqlConnectionProvider connections,
            String table,
            String insertSql,
            String updateSql,
            RowMapper<T> rowMapper,
            Function<T, Object[]> values,
            ToLongFunction<T> id,
            BiFunction<T, Long, T> withId) {
        this.connections = connections;
        this.table = table;
        this.insertSql = insertSql;
        this.updateSql = updateSql;
        this.rowMapper = rowMapper;
        this.values = values;
        this.id = id;
        this.withId = withId;
    }

    @Override
    public Optional<T> findById(long id) throws SQLException {
        String sql = "SELECT * FROM " + table + " WHERE id = ?";
        try (var connection = connections.getConnection();
             var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (var resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(rowMapper.map(resultSet)) : Optional.empty();
            }
        }
    }

    @Override
    public List<T> findAll() throws SQLException {
        return query("SELECT * FROM " + table + " ORDER BY id");
    }

    protected final List<T> query(String sql, Object... parameters) throws SQLException {
        List<T> results = new ArrayList<>();
        try (var connection = connections.getConnection();
             var statement = connection.prepareStatement(sql)) {
            bind(statement, parameters);
            try (var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    results.add(rowMapper.map(resultSet));
                }
            }
        }
        return results;
    }

    @Override
    public T save(T entity) throws SQLException {
        Object[] parameters = values.apply(entity);
        try (var connection = connections.getConnection();
             var statement = connection.prepareStatement(insertSql)) {
            bind(statement, parameters);
            try (var resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new SQLException("Insert into " + table + " did not return an id.");
                }
                return withId.apply(entity, resultSet.getLong("id"));
            }
        }
    }

    @Override
    public boolean update(T entity) throws SQLException {
        Object[] parameters = values.apply(entity);
        try (var connection = connections.getConnection();
             var statement = connection.prepareStatement(updateSql)) {
            bind(statement, parameters);
            statement.setLong(parameters.length + 1, id.applyAsLong(entity));
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteById(long id) throws SQLException {
        String sql = "DELETE FROM " + table + " WHERE id = ?";
        try (var connection = connections.getConnection();
             var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    private static void bind(PreparedStatement statement, Object[] parameters) throws SQLException {
        for (int index = 0; index < parameters.length; index++) {
            statement.setObject(index + 1, parameters[index]);
        }
    }

    protected static long requiredId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("An id is required to update an entity.");
        }
        return id;
    }
}
