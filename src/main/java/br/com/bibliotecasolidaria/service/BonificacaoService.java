package br.com.bibliotecasolidaria.service;

import br.com.bibliotecasolidaria.model.Bonificacao;
import br.com.bibliotecasolidaria.model.Doacao;
import br.com.bibliotecasolidaria.model.Doador;
import br.com.bibliotecasolidaria.strategy.BonificacaoStrategy;
import java.util.ArrayList;
import java.util.List;

public class BonificacaoService {
    private final List<BonificacaoStrategy> estrategias = new ArrayList<>();

    public void adicionarEstrategia(BonificacaoStrategy e) {
        estrategias.add(e);
    }

    public List<Bonificacao> processar(Doador doador, Doacao doacao) {
        List<Bonificacao> concedidas = new ArrayList<>();
        for (BonificacaoStrategy strat : estrategias) {
            strat.avaliar(doador, doacao).ifPresent(concedidas::add);
        }
        return concedidas;
    }
}
