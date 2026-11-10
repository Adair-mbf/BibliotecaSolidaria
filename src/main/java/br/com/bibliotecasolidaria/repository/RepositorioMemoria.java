package br.com.bibliotecasolidaria.repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public abstract class RepositorioMemoria<T, ID> implements Repositorio<T, ID> {
    protected final Map<ID, T> storage = new ConcurrentHashMap<>();
    protected final AtomicLong idSeq = new AtomicLong(1);

    @Override
    public Optional<T> buscarPorId(ID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<T> listarTodos() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void remover(ID id) {
        storage.remove(id);
    }
}
