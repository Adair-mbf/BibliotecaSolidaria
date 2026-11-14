package br.com.bibliotecasolidaria.persistence;

import br.com.bibliotecasolidaria.exception.ErroPersistenciaException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/** Executa {@code db/schema.sql} (do classpath) no banco configurado. */
public final class InicializadorSchema {
    private InicializadorSchema() { }

    public static void executar(SqlConnectionProvider conexoes) {
        String script;
        try (InputStream in = InicializadorSchema.class.getResourceAsStream("/db/schema.sql")) {
            if (in == null) {
                throw new ErroPersistenciaException("Arquivo db/schema.sql não encontrado no classpath.");
            }
            script = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ErroPersistenciaException("Falha ao ler o schema: " + e.getMessage(), e);
        }
        try {
            Connection c = conexoes.getConnection();
            for (String comando : removerComentarios(script).split(";")) {
                String sql = comando.trim();
                if (!sql.isEmpty()) {
                    try (Statement st = c.createStatement()) {
                        st.execute(sql);
                    }
                }
            }
        } catch (SQLException e) {
            throw new ErroPersistenciaException("Falha ao criar o schema: " + e.getMessage(), e);
        }
    }

    private static String removerComentarios(String sql) {
        StringBuilder sb = new StringBuilder();
        for (String linha : sql.split("\n")) {
            if (!linha.trim().startsWith("--")) {
                sb.append(linha).append('\n');
            }
        }
        return sb.toString();
    }
}
