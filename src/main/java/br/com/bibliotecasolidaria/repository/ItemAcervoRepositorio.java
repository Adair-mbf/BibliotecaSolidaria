package br.com.bibliotecasolidaria.repository;

import br.com.bibliotecasolidaria.model.ItemAcervo;
import java.util.List;
import java.util.stream.Collectors;

public class ItemAcervoRepositorio extends RepositorioMemoria<ItemAcervo, Long> {
    @Override
    public ItemAcervo salvar(ItemAcervo entidade) {
        if (entidade.getId() == null) {
            entidade.setId(idSeq.getAndIncrement());
        }
        storage.put(entidade.getId(), entidade);
        return entidade;
    }

    public List<ItemAcervo> listarDisponiveis() {
        return storage.values().stream()
                .filter(ItemAcervo::estaDisponivel)
                .collect(Collectors.toList());
    }
}
