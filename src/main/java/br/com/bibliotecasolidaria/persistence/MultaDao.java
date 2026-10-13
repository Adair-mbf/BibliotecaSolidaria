package br.com.bibliotecasolidaria.persistence;

import br.com.bibliotecasolidaria.model.Domain.Multa;
import br.com.bibliotecasolidaria.model.Domain.StatusMulta;

public final class MultaDao extends JdbcCrudDao<Multa> {
    public MultaDao(SqlConnectionProvider connections) {
        super(
                connections,
                "multas",
                "INSERT INTO multas (emprestimo_id, valor, data_geracao, data_pagamento, status) "
                        + "VALUES (?, ?, ?, ?, ?) RETURNING id",
                "UPDATE multas SET emprestimo_id = ?, valor = ?, data_geracao = ?, data_pagamento = ?, "
                        + "status = ? WHERE id = ?",
                resultSet -> new Multa(
                        resultSet.getLong("id"),
                        resultSet.getObject("emprestimo_id", Long.class),
                        resultSet.getBigDecimal("valor"),
                        resultSet.getObject("data_geracao", java.time.LocalDate.class),
                        resultSet.getObject("data_pagamento", java.time.LocalDate.class),
                        StatusMulta.valueOf(resultSet.getString("status"))),
                multa -> new Object[]{
                        multa.emprestimoId(), multa.valor(), multa.dataGeracao(), multa.dataPagamento(),
                        multa.status().name()
                },
                multa -> requiredId(multa.id()),
                (multa, id) -> new Multa(
                        id, multa.emprestimoId(), multa.valor(), multa.dataGeracao(),
                        multa.dataPagamento(), multa.status()));
    }
}
