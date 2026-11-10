package br.com.bibliotecasolidaria.repository;

import br.com.bibliotecasolidaria.model.Doacao;
import java.util.List;
import java.util.stream.Collectors;

public class DoacaoRepositorio extends RepositorioMemoria<Doacao, Long> {
    @Override
    public Doacao salvar(Doacao entidade) {
        if (entidade.getId() == null) {
            entidade.setId(idSeq.getAndIncrement());
        }
        storage.put(entidade.getId(), entidade);
        return entidade;
    }

    public List<Doacao> listarPorDoador(Long idDoador) {
        return storage.values().stream()
                .filter(d -> d.getDoadorId() != null && d.getDoadorId().equals(idDoador))
                .collect(Collectors.toList());
    }
}
