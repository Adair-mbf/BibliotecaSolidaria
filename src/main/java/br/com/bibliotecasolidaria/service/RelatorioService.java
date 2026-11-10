package br.com.bibliotecasolidaria.service;

import br.com.bibliotecasolidaria.repository.DoacaoRepositorio;
import br.com.bibliotecasolidaria.repository.EmprestimoRepositorio;
import java.util.Map;

public class RelatorioService {
    private final EmprestimoRepositorio emprestimoRepo;
    private final DoacaoRepositorio doacaoRepo;

    public RelatorioService(EmprestimoRepositorio er, DoacaoRepositorio dr) {
        this.emprestimoRepo = er;
        this.doacaoRepo = dr;
    }

    public Map<String, Object> gerarRelatorioGeral() {
        return Map.of(
            "totalEmprestimos", emprestimoRepo.listarTodos().size(),
            "totalDoacoes", doacaoRepo.listarTodos().size(),
            "emprestimosAtrasados", emprestimoRepo.listarAtrasados().size()
        );
    }
}
