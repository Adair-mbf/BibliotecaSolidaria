package br.com.bibliotecasolidaria.persistence;

import br.com.bibliotecasolidaria.model.Domain.CategoriaLivro;
import br.com.bibliotecasolidaria.model.Domain.Doacao;
import br.com.bibliotecasolidaria.model.Domain.EstadoConservacao;
import br.com.bibliotecasolidaria.model.Domain.StatusDoacao;
import br.com.bibliotecasolidaria.model.Domain.TipoDoacao;

public final class DoacaoDao extends JdbcCrudDao<Doacao> {
    private static final String COLUMNS =
            "doador_id, data_registro, status, tipo, titulo, autor, isbn, categoria, estado, quantidade, "
                    + "arquivo_url, formato, valor, meio_pagamento, comprovante";

    public DoacaoDao(SqlConnectionProvider connections) {
        super(
                connections,
                "doacoes",
                "INSERT INTO doacoes (" + COLUMNS + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id",
                "UPDATE doacoes SET doador_id = ?, data_registro = ?, status = ?, tipo = ?, titulo = ?, "
                        + "autor = ?, isbn = ?, categoria = ?, estado = ?, quantidade = ?, arquivo_url = ?, "
                        + "formato = ?, valor = ?, meio_pagamento = ?, comprovante = ? WHERE id = ?",
                resultSet -> new Doacao(
                        resultSet.getLong("id"),
                        resultSet.getObject("doador_id", Long.class),
                        resultSet.getObject("data_registro", java.time.LocalDate.class),
                        StatusDoacao.valueOf(resultSet.getString("status")),
                        TipoDoacao.valueOf(resultSet.getString("tipo")),
                        resultSet.getString("titulo"),
                        resultSet.getString("autor"),
                        resultSet.getString("isbn"),
                        enumOrNull(resultSet.getString("categoria"), CategoriaLivro.class),
                        enumOrNull(resultSet.getString("estado"), EstadoConservacao.class),
                        resultSet.getObject("quantidade", Integer.class),
                        resultSet.getString("arquivo_url"),
                        resultSet.getString("formato"),
                        resultSet.getBigDecimal("valor"),
                        resultSet.getString("meio_pagamento"),
                        resultSet.getString("comprovante")),
                doacao -> new Object[]{
                        doacao.doadorId(), doacao.dataRegistro(), doacao.status().name(), doacao.tipo().name(),
                        doacao.titulo(), doacao.autor(), doacao.isbn(), name(doacao.categoria()),
                        name(doacao.estado()), doacao.quantidade(), doacao.arquivoUrl(), doacao.formato(),
                        doacao.valor(), doacao.meioPagamento(), doacao.comprovante()
                },
                doacao -> requiredId(doacao.id()),
                (doacao, id) -> new Doacao(
                        id, doacao.doadorId(), doacao.dataRegistro(), doacao.status(), doacao.tipo(),
                        doacao.titulo(), doacao.autor(), doacao.isbn(), doacao.categoria(), doacao.estado(),
                        doacao.quantidade(), doacao.arquivoUrl(), doacao.formato(), doacao.valor(),
                        doacao.meioPagamento(), doacao.comprovante()));
    }

    private static String name(Enum<?> value) {
        return value == null ? null : value.name();
    }

    private static <E extends Enum<E>> E enumOrNull(String value, Class<E> type) {
        return value == null ? null : Enum.valueOf(type, value);
    }
}
