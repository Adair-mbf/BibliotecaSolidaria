package br.com.bibliotecasolidaria.persistence;

import java.sql.Connection;
import java.sql.SQLException;

/** Fornece a conexão usada pelos repositórios. Os repositórios nunca a fecham. */
@FunctionalInterface
public interface SqlConnectionProvider {
    Connection getConnection() throws SQLException;
}
