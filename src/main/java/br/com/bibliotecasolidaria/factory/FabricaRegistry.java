package br.com.bibliotecasolidaria.factory;

import br.com.bibliotecasolidaria.exception.ValidacaoException;
import br.com.bibliotecasolidaria.model.enums.TipoUsuario;

import java.util.EnumMap;
import java.util.Map;

/** Registro de fábricas por tipo de usuário. */
public class FabricaRegistry {
    private final Map<TipoUsuario, UsuarioFactory> fabricas = new EnumMap<>(TipoUsuario.class);

    public void registrar(TipoUsuario tipo, UsuarioFactory fabrica) {
        if (fabrica.tipoSuportado() != tipo) {
            throw new IllegalArgumentException("A fábrica suporta " + fabrica.tipoSuportado() + ", não " + tipo + ".");
        }
        fabricas.put(tipo, fabrica);
    }

    public UsuarioFactory obter(TipoUsuario tipo) {
        UsuarioFactory fabrica = tipo == null ? null : fabricas.get(tipo);
        if (fabrica == null) {
            throw new ValidacaoException("Tipo de usuário sem fábrica registrada: " + tipo + ".");
        }
        return fabrica;
    }
}
