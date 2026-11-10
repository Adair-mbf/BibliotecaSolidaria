package br.com.bibliotecasolidaria.repository;

import br.com.bibliotecasolidaria.model.CategoriaLivro;
import br.com.bibliotecasolidaria.model.Livro;
import java.util.List;
import java.util.stream.Collectors;

public class LivroRepositorio extends RepositorioMemoria<Livro, Long> {
    @Override
    public Livro salvar(Livro entidade) {
        if (entidade.getId() == null) {
            entidade.setId(idSeq.getAndIncrement());
        }
        storage.put(entidade.getId(), entidade);
        return entidade;
    }

    public List<Livro> buscarPorTitulo(String titulo) {
        return storage.values().stream()
                .filter(l -> l.getTitulo() != null && l.getTitulo().toLowerCase().contains(titulo.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Livro> buscarPorCategoria(CategoriaLivro c) {
        return storage.values().stream()
                .filter(l -> l.getCategoria() == c)
                .collect(Collectors.toList());
    }
}
