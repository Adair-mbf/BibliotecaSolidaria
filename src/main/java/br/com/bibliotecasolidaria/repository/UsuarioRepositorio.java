package br.com.bibliotecasolidaria.repository;

import br.com.bibliotecasolidaria.model.Usuario;
import java.util.Optional;

public class UsuarioRepositorio extends RepositorioMemoria<Usuario, Long> {
    @Override
    public Usuario salvar(Usuario entidade) {
        if (entidade.getId() == null) {
            entidade.setId(idSeq.getAndIncrement());
        }
        storage.put(entidade.getId(), entidade);
        return entidade;
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return storage.values().stream()
                .filter(u -> u.getEmail() != null && u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }
}
