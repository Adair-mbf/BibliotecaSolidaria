package br.com.bibliotecasolidaria.service;

import br.com.bibliotecasolidaria.model.Emprestimo;
import br.com.bibliotecasolidaria.repository.EmprestimoRepositorio;
import br.com.bibliotecasolidaria.strategy.PoliticaRenovacao;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class EmprestimoService {
    private final EmprestimoRepositorio emprestimoRepo;
    private final PoliticaRenovacao politicaRenovacao;

    public EmprestimoService(EmprestimoRepositorio repo, PoliticaRenovacao politica) {
        this.emprestimoRepo = repo;
        this.politicaRenovacao = politica;
    }

    public Emprestimo salvar(Emprestimo emp) {
        return emprestimoRepo.salvar(emp);
    }

    public Optional<Emprestimo> buscarPorId(Long id) {
        return emprestimoRepo.buscarPorId(id);
    }

    public List<Emprestimo> listarAtrasados() {
        return emprestimoRepo.listarAtrasados();
    }

    public Emprestimo renovar(Long idEmprestimo) {
        Emprestimo emp = emprestimoRepo.buscarPorId(idEmprestimo)
                .orElseThrow(() -> new IllegalArgumentException("Empréstimo não encontrado."));
        if (!politicaRenovacao.podeRenovar(emp)) {
            throw new IllegalStateException("Empréstimo não atende aos critérios para renovação.");
        }
        LocalDate novoPrazo = politicaRenovacao.calcularNovoPrazo(emp);
        emp.renovar(novoPrazo);
        return emprestimoRepo.salvar(emp);
    }
}
