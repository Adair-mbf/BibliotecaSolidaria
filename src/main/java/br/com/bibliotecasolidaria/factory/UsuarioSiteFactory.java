package br.com.bibliotecasolidaria.factory;

import br.com.bibliotecasolidaria.dto.DadosUsuario;
import br.com.bibliotecasolidaria.model.Domain.TipoUsuario;
import br.com.bibliotecasolidaria.model.Domain.Usuario;
import br.com.bibliotecasolidaria.persistence.UsuarioDao;

/** Concrete Creator para usuários do site. */
public final class UsuarioSiteFactory extends UsuarioFactory {
    public UsuarioSiteFactory(UsuarioDao usuarios) { super(usuarios); }

    @Override
    protected Usuario criarUsuario(DadosUsuario dados) {
        return usuarioBase(dados, TipoUsuario.USUARIO_SITE, null, null,
                dados.atributoObrigatorio("telefone"),
                dados.atributoObrigatorio("endereco"), null, null);
    }

    @Override
    protected TipoUsuario tipoSuportado() { return TipoUsuario.USUARIO_SITE; }
}
