package br.com.bibliotecasolidaria.persistence;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface Dao<T> {
    Optional<T> findById(long id) throws SQLException;

    List<T> findAll() throws SQLException;

    T save(T entity) throws SQLException;

    boolean update(T entity) throws SQLException;

    boolean deleteById(long id) throws SQLException;
}
