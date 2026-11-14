package br.com.bibliotecasolidaria.strategy;

import br.com.bibliotecasolidaria.model.Bonificacao;
import br.com.bibliotecasolidaria.model.Doacao;
import br.com.bibliotecasolidaria.model.Doador;

import java.util.Optional;

/** Strategy: critério de concessão de bonificação para uma doação aceita. */
public interface BonificacaoStrategy {
    /** Retorna a bonificação (ainda não persistida) merecida pela doação, se houver. */
    Optional<Bonificacao> avaliar(Doador doador, Doacao doacao);
}
