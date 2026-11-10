package br.com.bibliotecasolidaria.service;

import br.com.bibliotecasolidaria.model.Doacao;
import br.com.bibliotecasolidaria.repository.DoacaoRepositorio;

public class DoacaoService {
    private final DoacaoRepositorio doacaoRepo;

    public DoacaoService(DoacaoRepositorio repo) {
        this.doacaoRepo = repo;
    }

    public Doacao salvar(Doacao doacao) {
        return doacaoRepo.salvar(doacao);
    }

    public void aceitar(Long id) {
        Doacao d = doacaoRepo.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Doação não encontrada."));
        d.aceitar();
        doacaoRepo.salvar(d);
    }
}
