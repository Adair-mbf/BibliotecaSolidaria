package br.com.bibliotecasolidaria.persistence;

import br.com.bibliotecasolidaria.model.Domain.Bonificacao;
import br.com.bibliotecasolidaria.model.Domain.TipoBonificacao;

public final class BonificacaoDao extends JdbcCrudDao<Bonificacao> {
    public BonificacaoDao(SqlConnectionProvider connections) {
        super(
                connections,
                "bonificacoes",
                "INSERT INTO bonificacoes (doador_id, tipo, descricao, data_concessao) "
                        + "VALUES (?, ?, ?, ?) RETURNING id",
                "UPDATE bonificacoes SET doador_id = ?, tipo = ?, descricao = ?, data_concessao = ? WHERE id = ?",
                resultSet -> new Bonificacao(
                        resultSet.getLong("id"),
                        resultSet.getObject("doador_id", Long.class),
                        TipoBonificacao.valueOf(resultSet.getString("tipo")),
                        resultSet.getString("descricao"),
                        resultSet.getObject("data_concessao", java.time.LocalDate.class)),
                bonificacao -> new Object[]{
                        bonificacao.doadorId(), bonificacao.tipo().name(), bonificacao.descricao(),
                        bonificacao.dataConcessao()
                },
                bonificacao -> requiredId(bonificacao.id()),
                (bonificacao, id) -> new Bonificacao(
                        id, bonificacao.doadorId(), bonificacao.tipo(), bonificacao.descricao(),
                        bonificacao.dataConcessao()));
    }
}
