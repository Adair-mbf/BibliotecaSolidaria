package br.com.bibliotecasolidaria.strategy;

import br.com.bibliotecasolidaria.model.*;
import java.time.LocalDate;
import java.util.Optional;

public class BonificacaoEmpresarial implements BonificacaoStrategy {
    private int quantidadeMinimaPJ;

    public BonificacaoEmpresarial(int quantidadeMinimaPJ) {
        this.quantidadeMinimaPJ = quantidadeMinimaPJ;
    }

    @Override
    public Optional<Bonificacao> avaliar(Doador doador, Doacao doacao) {
        if (doador != null && doador.isPessoaJuridica()) {
            int qtd = 1;
            if (doacao instanceof DoacaoLivroFisico f) {
                qtd = f.getQuantidade();
            }
            if (qtd >= quantidadeMinimaPJ) {
                return Optional.of(new Bonificacao(null, doador.getId(), TipoBonificacao.PARCERIA,
                        "Selo de Parceria Cidadã Empresarial para " + doador.getNome(),
                        LocalDate.now()));
            }
        }
        return Optional.empty();
    }
}
