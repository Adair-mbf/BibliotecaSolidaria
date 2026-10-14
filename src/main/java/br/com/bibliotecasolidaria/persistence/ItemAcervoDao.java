package br.com.bibliotecasolidaria.persistence;

import br.com.bibliotecasolidaria.model.Domain.EstadoConservacao;
import br.com.bibliotecasolidaria.model.Domain.ItemAcervo;
import br.com.bibliotecasolidaria.model.Domain.StatusItem;
import br.com.bibliotecasolidaria.model.Domain.TipoItemAcervo;

public final class ItemAcervoDao extends JdbcCrudDao<ItemAcervo> {
    public ItemAcervoDao(SqlConnectionProvider connections) {
        super(
                connections,
                "itens_acervo",
                "INSERT INTO itens_acervo (livro_id, tipo, status, codigo_tombo, estado, localizacao, "
                        + "arquivo_url, formato) VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id",
                "UPDATE itens_acervo SET livro_id = ?, tipo = ?, status = ?, codigo_tombo = ?, estado = ?, "
                        + "localizacao = ?, arquivo_url = ?, formato = ? WHERE id = ?",
                resultSet -> new ItemAcervo(
                        resultSet.getLong("id"),
                        resultSet.getObject("livro_id", Long.class),
                        TipoItemAcervo.valueOf(resultSet.getString("tipo")),
                        StatusItem.valueOf(resultSet.getString("status")),
                        resultSet.getString("codigo_tombo"),
                        enumOrNull(resultSet.getString("estado"), EstadoConservacao.class),
                        resultSet.getString("localizacao"),
                        resultSet.getString("arquivo_url"),
                        resultSet.getString("formato")),
                item -> new Object[]{
                        item.livroId(), item.tipo().name(), item.status().name(), item.codigoTombo(),
                        name(item.estado()), item.localizacao(), item.arquivoUrl(), item.formato()
                },
                item -> requiredId(item.id()),
                (item, id) -> new ItemAcervo(
                        id, item.livroId(), item.tipo(), item.status(), item.codigoTombo(), item.estado(),
                        item.localizacao(), item.arquivoUrl(), item.formato()));
    }

    private static String name(Enum<?> value) {
        return value == null ? null : value.name();
    }

    private static <E extends Enum<E>> E enumOrNull(String value, Class<E> type) {
        return value == null ? null : Enum.valueOf(type, value);
    }
}
