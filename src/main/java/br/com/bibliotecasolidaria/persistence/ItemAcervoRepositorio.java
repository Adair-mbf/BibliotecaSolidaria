package br.com.bibliotecasolidaria.persistence;

import br.com.bibliotecasolidaria.model.Ebook;
import br.com.bibliotecasolidaria.model.ExemplarFisico;
import br.com.bibliotecasolidaria.model.ItemAcervo;
import br.com.bibliotecasolidaria.model.enums.EstadoConservacao;
import br.com.bibliotecasolidaria.model.enums.StatusItem;
import br.com.bibliotecasolidaria.model.enums.TipoItemAcervo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public final class ItemAcervoRepositorio extends RepositorioJdbcLong<ItemAcervo> {
    public ItemAcervoRepositorio(SqlConnectionProvider conexoes) { super(conexoes); }

    @Override protected String tabela() { return "itens_acervo"; }

    @Override protected String sqlInsert() {
        return "INSERT INTO itens_acervo (livro_id, tipo, status, codigo_tombo, estado, localizacao, arquivo_url, "
                + "formato) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    }

    @Override protected String sqlUpdate() {
        return "UPDATE itens_acervo SET livro_id = ?, tipo = ?, status = ?, codigo_tombo = ?, estado = ?, "
                + "localizacao = ?, arquivo_url = ?, formato = ? WHERE id = ?";
    }

    @Override protected Long getId(ItemAcervo i) { return i.getId(); }

    @Override protected void setId(ItemAcervo i, Long id) { i.setId(id); }

    @Override
    protected Object[] valores(ItemAcervo i) {
        Object[] v = {i.getLivroId(), i.getTipo().name(), i.getStatus().name(), null, null, null, null, null};
        if (i instanceof ExemplarFisico e) {
            v[3] = e.getCodigoTombo(); v[4] = nome(e.getEstado()); v[5] = e.getLocalizacao();
        } else if (i instanceof Ebook b) {
            v[6] = b.getArquivoUrl(); v[7] = b.getFormato();
        }
        return v;
    }

    @Override
    protected ItemAcervo mapear(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        Long livro = rs.getObject("livro_id", Long.class);
        StatusItem status = StatusItem.valueOf(rs.getString("status"));
        return switch (TipoItemAcervo.valueOf(rs.getString("tipo"))) {
            case EXEMPLAR_FISICO -> new ExemplarFisico(id, livro, status, rs.getString("codigo_tombo"),
                    enumOuNulo(rs.getString("estado"), EstadoConservacao.class), rs.getString("localizacao"));
            case EBOOK -> new Ebook(id, livro, status, rs.getString("arquivo_url"), rs.getString("formato"));
        };
    }

    /** Exemplares com status DISPONIVEL e todos os e-books (licença sempre disponível). */
    public List<ItemAcervo> listarDisponiveis() {
        return consultar("SELECT * FROM itens_acervo WHERE status = 'DISPONIVEL' OR tipo = 'EBOOK' ORDER BY id");
    }

    public List<ItemAcervo> listarPorLivro(long livroId) {
        return consultar("SELECT * FROM itens_acervo WHERE livro_id = ? ORDER BY id", livroId);
    }

    public Optional<ItemAcervo> buscarPorCodigoTombo(String codigo) {
        return consultarUm("SELECT * FROM itens_acervo WHERE codigo_tombo = ?", codigo);
    }
}
