package br.com.bibliotecasolidaria.repository;

import java.util.List;
import java.util.Optional;

public interface Repositorio<T, ID> {
    T salvar(T entidade);
    Optional<T> buscarPorId(ID id);
    List<T> listarTodos();
    void remover(ID id);
}
