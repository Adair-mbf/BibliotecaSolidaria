package br.com.bibliotecasolidaria.factory;

import br.com.bibliotecasolidaria.dto.DadosUsuario;
import br.com.bibliotecasolidaria.model.Domain.TipoUsuario;
import br.com.bibliotecasolidaria.model.Domain.Usuario;
import br.com.bibliotecasolidaria.persistence.UsuarioDao;

/** Concrete Creator para usuários do tipo doador. */
public final class DoadorFactory extends UsuarioFactory {
    public DoadorFactory(UsuarioDao usuarios) { super(usuarios); }

    @Override
    protected Usuario criarUsuario(DadosUsuario dados) {
        return usuarioBase(dados, TipoUsuario.DOADOR,
                dados.atributoObrigatorio("documento"),
                dados.atributoBooleano("pessoaJuridica"),
                null, null, null, null);
    }

    @Override
    protected TipoUsuario tipoSuportado() { return TipoUsuario.DOADOR; }
}
