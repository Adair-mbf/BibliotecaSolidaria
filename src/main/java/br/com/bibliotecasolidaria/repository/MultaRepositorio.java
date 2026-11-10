package br.com.bibliotecasolidaria.repository;

import br.com.bibliotecasolidaria.model.Multa;
import br.com.bibliotecasolidaria.model.StatusMulta;
import java.util.List;
import java.util.stream.Collectors;

public class MultaRepositorio extends RepositorioMemoria<Multa, Long> {
    @Override
    public Multa salvar(Multa entidade) {
        if (entidade.getId() == null) {
            entidade.setId(idSeq.getAndIncrement());
        }
        storage.put(entidade.getId(), entidade);
        return entidade;
    }

    public List<Multa> listarPendentes(Long idUsuario) {
        return storage.values().stream()
                .filter(m -> m.getUsuarioId() != null && m.getUsuarioId().equals(idUsuario) && m.getStatus() == StatusMulta.PENDENTE)
                .collect(Collectors.toList());
    }
}
