package br.com.bibliotecasolidaria.persistence;

import java.util.List;
import java.util.Optional;

/** Contrato genérico de repositório. {@code salvar} insere quando o id é nulo e atualiza caso contrário. */
public interface Repositorio<T, ID> {
    T salvar(T entidade);

    Optional<T> buscarPorId(ID id);

    List<T> listarTodos();

    /** Remove o registro; não faz nada se ele não existir. */
    void remover(ID id);
}
