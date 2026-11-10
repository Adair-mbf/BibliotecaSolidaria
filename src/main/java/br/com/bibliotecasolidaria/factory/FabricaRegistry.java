package br.com.bibliotecasolidaria.factory;

import br.com.bibliotecasolidaria.model.TipoUsuario;
import java.util.EnumMap;
import java.util.Map;

public class FabricaRegistry {
    private final Map<TipoUsuario, UsuarioFactory> fabricas = new EnumMap<>(TipoUsuario.class);

    public FabricaRegistry() {
        registrar(TipoUsuario.DOADOR, new DoadorFactory());
        registrar(TipoUsuario.USUARIO_SITE, new UsuarioSiteFactory());
        registrar(TipoUsuario.FUNCIONARIO, new FuncionarioFactory());
    }

    public void registrar(TipoUsuario tipo, UsuarioFactory f) {
        fabricas.put(tipo, f);
    }

    public UsuarioFactory obter(TipoUsuario tipo) {
        UsuarioFactory f = fabricas.get(tipo);
        if (f == null) {
            throw new IllegalArgumentException("Nenhuma fábrica registrada para o tipo: " + tipo);
        }
        return f;
    }
}
