package br.com.bibliotecasolidaria.persistence;

/** Especialização para tabelas com chave {@code BIGINT} gerada pelo banco. */
public abstract class RepositorioJdbcLong<T> extends RepositorioJdbc<T, Long> {
    protected RepositorioJdbcLong(SqlConnectionProvider conexoes) {
        super(conexoes);
    }

    @Override
    protected Long idGerado(long valor) {
        return valor;
    }
}
