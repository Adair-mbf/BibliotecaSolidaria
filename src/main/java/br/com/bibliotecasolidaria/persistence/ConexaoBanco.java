package br.com.bibliotecasolidaria.persistence;

import br.com.bibliotecasolidaria.exception.ErroPersistenciaException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.function.Supplier;

/**
 * Singleton que mantém uma única conexão JDBC e controla transações.
 * <p>
 * Todas as operações compartilham a mesma {@link Connection}; por isso, a classe serializa o acesso
 * transacional (um bloco {@link #emTransacao} por vez) e é adequada a uma aplicação de console.
 * Configuração via {@code DB_URL}, {@code DB_USER} e {@code DB_PASSWORD}.
 */
public final class ConexaoBanco implements SqlConnectionProvider, ExecutorTransacional {
    private static ConexaoBanco instancia;

    private final String url;
    private final String usuario;
    private final String senha;
    private Connection conexao;
    private int profundidade;

    private ConexaoBanco(String url, String usuario, String senha) {
        this.url = exigir(url, "DB_URL");
        this.usuario = exigir(usuario, "DB_USER");
        this.senha = exigir(senha, "DB_PASSWORD");
    }

    /** Instância única configurada pelo ambiente (thread-safe, criada sob demanda). */
    public static synchronized ConexaoBanco getInstancia() {
    if (instancia == null) {
        instancia = new ConexaoBanco(
                "jdbc:postgresql://localhost:5432/bd_BibliotecaSolidaria",
                "postgres",
                "minhasenha123");
    }
    return instancia;
}

    /** Cria uma conexão independente da singleton, para testes ou outro banco. */
    public static ConexaoBanco novaInstancia(String url, String usuario, String senha) {
        return new ConexaoBanco(url, usuario, senha);
    }

    public synchronized Connection getConexao() throws SQLException {
        if (conexao == null || conexao.isClosed()) {
            conexao = DriverManager.getConnection(url, usuario, senha);
            profundidade = 0;
        }
        return conexao;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return getConexao();
    }

    public synchronized void fechar() {
        try {
            if (conexao != null && !conexao.isClosed()) {
                conexao.close();
            }
        } catch (SQLException e) {
            throw new ErroPersistenciaException("Falha ao fechar a conexão: " + e.getMessage(), e);
        } finally {
            conexao = null;
            profundidade = 0;
        }
    }

    @Override
    public synchronized <T> T emTransacao(Supplier<T> acao) {
        if (profundidade > 0) { // já dentro de uma transação: participa dela
            profundidade++;
            try {
                return acao.get();
            } finally {
                profundidade--;
            }
        }
        Connection c;
        try {
            c = getConexao();
            c.setAutoCommit(false);
        } catch (SQLException e) {
            throw new ErroPersistenciaException("Não foi possível iniciar a transação: " + e.getMessage(), e);
        }
        profundidade = 1;
        try {
            T resultado = acao.get();
            c.commit();
            return resultado;
        } catch (SQLException e) {
            desfazer(c);
            throw new ErroPersistenciaException("Falha ao confirmar a transação: " + e.getMessage(), e);
        } catch (RuntimeException | Error e) {
            desfazer(c);
            throw e;
        } finally {
            profundidade = 0;
            try {
                c.setAutoCommit(true);
            } catch (SQLException ignorada) {
                // conexão possivelmente inválida; será recriada no próximo uso
            }
        }
    }

    private static void desfazer(Connection c) {
        try {
            c.rollback();
        } catch (SQLException ignorada) {
            // nada a fazer: o erro original é o relevante
        }
    }

    private static String exigir(String valor, String nome) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("A configuração " + nome + " do banco de dados é obrigatória.");
        }
        return valor;
    }
}
