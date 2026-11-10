package br.com.bibliotecasolidaria.service;

import br.com.bibliotecasolidaria.model.Emprestimo;
import br.com.bibliotecasolidaria.model.TipoEvento;
import br.com.bibliotecasolidaria.observer.EventoBiblioteca;
import br.com.bibliotecasolidaria.observer.PublicadorEventos;
import java.util.List;
import java.util.Map;

public class AgendadorVencimentos {
    private final EmprestimoService emprestimoService;
    private final PublicadorEventos publicador;

    public AgendadorVencimentos(EmprestimoService es, PublicadorEventos pub) {
        this.emprestimoService = es;
        this.publicador = pub;
    }

    public void verificarVencimentosProximos() {
        List<Emprestimo> atrasados = emprestimoService.listarAtrasados();
        for (Emprestimo e : atrasados) {
            publicador.notificar(new EventoBiblioteca(TipoEvento.VENCIMENTO_PROXIMO, null,
                    "Empréstimo em atraso ou prestes a vencer: " + e.getId(), Map.of("emprestimoId", e.getId())));
        }
    }
}
