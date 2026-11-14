package br.com.bibliotecasolidaria.persistence;

import br.com.bibliotecasolidaria.exception.ConflitoException;
import br.com.bibliotecasolidaria.exception.ErroPersistenciaException;
import br.com.bibliotecasolidaria.exception.NaoEncontradoException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Implementação base JDBC: cada repositório concreto informa SQL, mapeamento e parâmetros. */
public abstract class RepositorioJdbc<T, ID> implements Repositorio<T, ID> {
    private final SqlConnectionProvider conexoes;

    protected RepositorioJdbc(SqlConnectionProvider conexoes) {
        this.conexoes = conexoes;
    }

    protected abstract String tabela();

    /** INSERT com um {@code ?} para cada valor retornado por {@link #valores}. */
    protected abstract String sqlInsert();

    /** UPDATE com os mesmos parâmetros de {@link #valores}, seguidos de {@code WHERE id = ?}. */
    protected abstract String sqlUpdate();

    protected abstract Object[] valores(T entidade);

    protected abstract T mapear(ResultSet rs) throws SQLException;

    protected abstract ID getId(T entidade);

    protected abstract void setId(T entidade, ID id);

    protected abstract ID idGerado(long valor);

    /** Conexão compartilhada: não deve ser fechada pelos repositórios. */
    protected Connection conexao() throws SQLException {
        return conexoes.getConnection();
    }

    @Override
    public T salvar(T entidade) {
        return getId(entidade) == null ? inserir(entidade) : atualizar(entidade);
    }

    private T inserir(T entidade) {
        try (PreparedStatement ps = conexao().prepareStatement(sqlInsert(), new String[]{"id"})) {
            vincular(ps, valores(entidade));
            ps.executeUpdate();
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (!chaves.next()) {
                    throw new ErroPersistenciaException("A inserção em " + tabela() + " não retornou o id.");
                }
                setId(entidade, idGerado(chaves.getLong(1)));
            }
            return entidade;
        } catch (SQLException e) {
            throw traduzir(e);
        }
    }

    private T atualizar(T entidade) {
        Object[] valores = valores(entidade);
        try (PreparedStatement ps = conexao().prepareStatement(sqlUpdate())) {
            vincular(ps, valores);
            ps.setObject(valores.length + 1, getId(entidade));
            if (ps.executeUpdate() == 0) {
                throw new NaoEncontradoException("Registro " + getId(entidade) + " não encontrado em " + tabela() + ".");
            }
            return entidade;
        } catch (SQLException e) {
            throw traduzir(e);
        }
    }

    @Override
    public Optional<T> buscarPorId(ID id) {
        return consultarUm("SELECT * FROM " + tabela() + " WHERE id = ?", id);
    }

    @Override
    public List<T> listarTodos() {
        return consultar("SELECT * FROM " + tabela() + " ORDER BY id");
    }

    @Override
    public void remover(ID id) {
        executar("DELETE FROM " + tabela() + " WHERE id = ?", id);
    }

    protected final List<T> consultar(String sql, Object... parametros) {
        List<T> resultados = new ArrayList<>();
        try (PreparedStatement ps = conexao().prepareStatement(sql)) {
            vincular(ps, parametros);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultados.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw traduzir(e);
        }
        return resultados;
    }

    protected final Optional<T> consultarUm(String sql, Object... parametros) {
        return consultar(sql, parametros).stream().findFirst();
    }

    protected final boolean existe(String sql, Object... parametros) {
        try (PreparedStatement ps = conexao().prepareStatement(sql)) {
            vincular(ps, parametros);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw traduzir(e);
        }
    }

    protected final int executar(String sql, Object... parametros) {
        try (PreparedStatement ps = conexao().prepareStatement(sql)) {
            vincular(ps, parametros);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw traduzir(e);
        }
    }

    private static void vincular(PreparedStatement ps, Object[] parametros) throws SQLException {
        for (int i = 0; i < parametros.length; i++) {
            ps.setObject(i + 1, parametros[i]);
        }
    }

    private RuntimeException traduzir(SQLException e) {
        String estado = e.getSQLState();
        if (estado != null && estado.startsWith("23")) { // violação de integridade (FK, unique, check)
            return new ConflitoException("Operação viola uma regra de integridade em " + tabela()
                    + " (registro duplicado, referenciado por outros ou com dados inconsistentes).", e);
        }
        return new ErroPersistenciaException("Erro de banco de dados em " + tabela() + ": " + e.getMessage(), e);
    }

    protected static <E extends Enum<E>> E enumOuNulo(String valor, Class<E> tipo) {
        return valor == null ? null : Enum.valueOf(tipo, valor);
    }

    protected static String nome(Enum<?> valor) {
        return valor == null ? null : valor.name();
    }
}
