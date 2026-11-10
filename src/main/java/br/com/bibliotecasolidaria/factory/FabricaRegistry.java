package br.com.bibliotecasolidaria.factory;

import br.com.bibliotecasolidaria.model.Domain.TipoUsuario;
import br.com.bibliotecasolidaria.persistence.UsuarioDao;

import java.util.EnumMap;
import java.util.Map;

/** Registro central das Concrete Creators de usuário. */
public final class FabricaRegistry {
    private final Map<TipoUsuario, UsuarioFactory> factories;

    public FabricaRegistry(UsuarioDao usuarios) {
        factories = new EnumMap<>(TipoUsuario.class);
        registrar(new DoadorFactory(usuarios));
        registrar(new UsuarioSiteFactory(usuarios));
        registrar(new FuncionarioFactory(usuarios));
    }

    public UsuarioFactory porTipo(TipoUsuario tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("O tipo de usuário é obrigatório.");
        }
        UsuarioFactory factory = factories.get(tipo);
        if (factory == null) {
            throw new IllegalArgumentException("Não existe factory registrada para " + tipo + ".");
        }
        return factory;
    }

    private void registrar(UsuarioFactory factory) {
        factories.put(factory.tipoSuportado(), factory);
    }
}
