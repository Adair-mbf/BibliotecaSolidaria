package br.com.bibliotecasolidaria.strategy;

import br.com.bibliotecasolidaria.model.Bonificacao;
import br.com.bibliotecasolidaria.model.Doacao;
import br.com.bibliotecasolidaria.model.Doador;
import java.util.Optional;

public interface BonificacaoStrategy {
    Optional<Bonificacao> avaliar(Doador doador, Doacao doacao);
}
