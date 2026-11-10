package br.com.bibliotecasolidaria.service;

import br.com.bibliotecasolidaria.model.Reserva;
import br.com.bibliotecasolidaria.repository.ReservaRepositorio;
import java.util.Optional;

public class ReservaService {
    private final ReservaRepositorio reservaRepo;

    public ReservaService(ReservaRepositorio repo) {
        this.reservaRepo = repo;
    }

    public Reserva salvar(Reserva r) {
        return reservaRepo.salvar(r);
    }

    public Optional<Reserva> buscarProxima(Long livroId) {
        return reservaRepo.proximaDaFila(livroId);
    }
}
