package br.com.bibliotecasolidaria.factory;

import br.com.bibliotecasolidaria.dto.DadosUsuario;
import br.com.bibliotecasolidaria.model.Usuario;
import br.com.bibliotecasolidaria.model.UsuarioSite;
import br.com.bibliotecasolidaria.model.enums.TipoUsuario;
import br.com.bibliotecasolidaria.persistence.UsuarioRepositorio;

/** Atributos: telefone e endereco (obrigatórios, exigidos pelo schema). */
public class UsuarioSiteFactory extends UsuarioFactory {
    public UsuarioSiteFactory(UsuarioRepositorio usuarios) {
        super(usuarios);
    }

    @Override public TipoUsuario tipoSuportado() { return TipoUsuario.USUARIO_SITE; }

    @Override
    protected void validar(DadosUsuario dados) {
        super.validar(dados);
        obrigatorio(dados, "telefone", "telefone");
        obrigatorio(dados, "endereco", "endereço");
    }

    @Override
    protected Usuario criarUsuario(DadosUsuario dados, String senhaHash) {
        return new UsuarioSite(null, dados.nome().trim(), dados.email().trim().toLowerCase(), senhaHash, true,
                dados.atributo("telefone").trim(), dados.atributo("endereco").trim());
    }
}
