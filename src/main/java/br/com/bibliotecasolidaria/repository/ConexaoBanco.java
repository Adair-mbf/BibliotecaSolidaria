package br.com.bibliotecasolidaria.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBanco {
    private static volatile ConexaoBanco instancia;
    private Connection connection;

    private ConexaoBanco() {
        try {
            String url = System.getenv().getOrDefault("DB_URL", "jdbc:h2:mem:biblioteca;DB_CLOSE_DELAY=-1");
            String user = System.getenv().getOrDefault("DB_USER", "sa");
            String pass = System.getenv().getOrDefault("DB_PASS", "");
            this.connection = DriverManager.getConnection(url, user, pass);
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao inicializar ConexaoBanco", e);
        }
    }

    public static ConexaoBanco getInstancia() {
        if (instancia == null) {
            synchronized (ConexaoBanco.class) {
                if (instancia == null) {
                    instancia = new ConexaoBanco();
                }
            }
        }
        return instancia;
    }

    public Connection getConexao() {
        return connection;
    }
}
