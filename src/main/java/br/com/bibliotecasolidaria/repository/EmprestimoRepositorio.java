package br.com.bibliotecasolidaria.repository;

import br.com.bibliotecasolidaria.model.Emprestimo;
import java.util.List;
import java.util.stream.Collectors;

public class EmprestimoRepositorio extends RepositorioMemoria<Emprestimo, Long> {
    @Override
    public Emprestimo salvar(Emprestimo entidade) {
        if (entidade.getId() == null) {
            entidade.setId(idSeq.getAndIncrement());
        }
        storage.put(entidade.getId(), entidade);
        return entidade;
    }

    public List<Emprestimo> listarAtrasados() {
        return storage.values().stream()
                .filter(Emprestimo::estaAtrasado)
                .collect(Collectors.toList());
    }

    public List<Emprestimo> listarPorUsuario(Long idUsuario) {
        return storage.values().stream()
                .filter(e -> e.getUsuarioId() != null && e.getUsuarioId().equals(idUsuario))
                .collect(Collectors.toList());
    }
}
