package br.com.bibliotecasolidaria.persistence;

import br.com.bibliotecasolidaria.model.Doacao;
import br.com.bibliotecasolidaria.model.DoacaoDinheiro;
import br.com.bibliotecasolidaria.model.DoacaoEbook;
import br.com.bibliotecasolidaria.model.DoacaoLivroFisico;
import br.com.bibliotecasolidaria.model.enums.CategoriaLivro;
import br.com.bibliotecasolidaria.model.enums.EstadoConservacao;
import br.com.bibliotecasolidaria.model.enums.StatusDoacao;
import br.com.bibliotecasolidaria.model.enums.TipoDoacao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public final class DoacaoRepositorio extends RepositorioJdbcLong<Doacao> {
    private static final String COLUNAS = "doador_id, data_registro, status, tipo, titulo, autor, isbn, "
            + "categoria, estado, quantidade, arquivo_url, formato, valor, meio_pagamento, comprovante";

    public DoacaoRepositorio(SqlConnectionProvider conexoes) { super(conexoes); }

    @Override protected String tabela() { return "doacoes"; }

    @Override protected String sqlInsert() {
        return "INSERT INTO doacoes (" + COLUNAS + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    @Override protected String sqlUpdate() {
        return "UPDATE doacoes SET doador_id = ?, data_registro = ?, status = ?, tipo = ?, titulo = ?, autor = ?, "
                + "isbn = ?, categoria = ?, estado = ?, quantidade = ?, arquivo_url = ?, formato = ?, valor = ?, "
                + "meio_pagamento = ?, comprovante = ? WHERE id = ?";
    }

    @Override protected Long getId(Doacao d) { return d.getId(); }

    @Override protected void setId(Doacao d, Long id) { d.setId(id); }

    @Override
    protected Object[] valores(Doacao d) {
        Object[] v = {d.getDoadorId(), d.getDataRegistro(), d.getStatus().name(), d.getTipo().name(),
                null, null, null, null, null, null, null, null, null, null, null};
        if (d instanceof DoacaoLivroFisico l) {
            v[4] = l.getTitulo(); v[5] = l.getAutor(); v[6] = l.getIsbn(); v[7] = nome(l.getCategoria());
            v[8] = nome(l.getEstado()); v[9] = l.getQuantidade();
        } else if (d instanceof DoacaoEbook e) {
            v[4] = e.getTitulo(); v[5] = e.getAutor(); v[10] = e.getArquivoUrl(); v[11] = e.getFormato();
        } else if (d instanceof DoacaoDinheiro m) {
            v[12] = m.getValor(); v[13] = m.getMeioPagamento(); v[14] = m.getComprovante();
        }
        return v;
    }

    @Override
    protected Doacao mapear(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        Long doador = rs.getObject("doador_id", Long.class);
        LocalDate data = rs.getObject("data_registro", LocalDate.class);
        StatusDoacao status = StatusDoacao.valueOf(rs.getString("status"));
        return switch (TipoDoacao.valueOf(rs.getString("tipo"))) {
            case LIVRO_FISICO -> new DoacaoLivroFisico(id, doador, data, status, rs.getString("titulo"),
                    rs.getString("autor"), rs.getString("isbn"),
                    enumOuNulo(rs.getString("categoria"), CategoriaLivro.class),
                    enumOuNulo(rs.getString("estado"), EstadoConservacao.class), rs.getInt("quantidade"));
            case EBOOK -> new DoacaoEbook(id, doador, data, status, rs.getString("titulo"),
                    rs.getString("autor"), rs.getString("arquivo_url"), rs.getString("formato"));
            case DINHEIRO -> new DoacaoDinheiro(id, doador, data, status, rs.getBigDecimal("valor"),
                    rs.getString("meio_pagamento"), rs.getString("comprovante"));
        };
    }

    public List<Doacao> listarPorDoador(long idDoador) {
        return consultar("SELECT * FROM doacoes WHERE doador_id = ? ORDER BY id", idDoador);
    }

    public List<Doacao> listarPorStatus(StatusDoacao status) {
        return consultar("SELECT * FROM doacoes WHERE status = ? ORDER BY id", status.name());
    }

    public List<Doacao> listarPorPeriodo(LocalDate inicio, LocalDate fim) {
        return consultar("SELECT * FROM doacoes WHERE data_registro BETWEEN ? AND ? ORDER BY data_registro, id",
                inicio, fim);
    }
}
