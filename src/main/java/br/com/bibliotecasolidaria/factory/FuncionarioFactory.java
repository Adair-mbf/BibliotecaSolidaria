package br.com.bibliotecasolidaria.factory;

import br.com.bibliotecasolidaria.dto.DadosUsuario;
import br.com.bibliotecasolidaria.model.Domain.TipoUsuario;
import br.com.bibliotecasolidaria.model.Domain.Usuario;
import br.com.bibliotecasolidaria.persistence.UsuarioDao;

/** Concrete Creator para funcionários. */
public final class FuncionarioFactory extends UsuarioFactory {
    public FuncionarioFactory(UsuarioDao usuarios) { super(usuarios); }

    @Override
    protected Usuario criarUsuario(DadosUsuario dados) {
        return usuarioBase(dados, TipoUsuario.FUNCIONARIO, null, null, null, null,
                dados.atributoObrigatorio("matricula"),
                dados.atributoObrigatorio("cargo"));
    }

    @Override
    protected TipoUsuario tipoSuportado() { return TipoUsuario.FUNCIONARIO; }
}
