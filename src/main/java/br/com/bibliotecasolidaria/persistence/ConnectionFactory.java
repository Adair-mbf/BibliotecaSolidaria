package br.com.bibliotecasolidaria.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

public final class ConnectionFactory implements SqlConnectionProvider {
    private final String url;
    private final String username;
    private final String password;

    public ConnectionFactory(String url, String username, String password) {
        this.url = requireValue(url, "url");
        this.username = requireValue(username, "username");
        this.password = requireValue(password, "password");
    }

    public static ConnectionFactory fromEnvironment() {
        Map<String, String> environment = System.getenv();
        return new ConnectionFactory(
                environment.get("DB_URL"),
                environment.get("DB_USER"),
                environment.get("DB_PASSWORD"));
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    private static String requireValue(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Database " + name + " must be configured.");
        }
        return value;
    }
}
