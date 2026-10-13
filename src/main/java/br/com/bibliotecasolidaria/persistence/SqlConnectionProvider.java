package br.com.bibliotecasolidaria.persistence;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface SqlConnectionProvider {
    Connection getConnection() throws SQLException;
}
