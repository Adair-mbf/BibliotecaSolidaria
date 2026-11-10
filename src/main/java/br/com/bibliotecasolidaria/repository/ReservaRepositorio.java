package br.com.bibliotecasolidaria.repository;

import br.com.bibliotecasolidaria.model.Reserva;
import br.com.bibliotecasolidaria.model.StatusReserva;
import java.util.Optional;

public class ReservaRepositorio extends RepositorioMemoria<Reserva, Long> {
    @Override
    public Reserva salvar(Reserva entidade) {
        if (entidade.getId() == null) {
            entidade.setId(idSeq.getAndIncrement());
        }
        storage.put(entidade.getId(), entidade);
        return entidade;
    }

    public Optional<Reserva> proximaDaFila(Long idLivro) {
        return storage.values().stream()
                .filter(r -> r.getLivroId() != null && r.getLivroId().equals(idLivro) && r.getStatus() == StatusReserva.ATIVA)
                .findFirst();
    }
}
