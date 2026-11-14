package br.com.bibliotecasolidaria.factory;

import br.com.bibliotecasolidaria.dto.DadosUsuario;
import br.com.bibliotecasolidaria.model.Doador;
import br.com.bibliotecasolidaria.model.Usuario;
import br.com.bibliotecasolidaria.model.enums.TipoUsuario;
import br.com.bibliotecasolidaria.persistence.UsuarioRepositorio;

/** Atributos: documento (obrigatório) e pessoaJuridica (true/false, padrão false). */
public class DoadorFactory extends UsuarioFactory {
    public DoadorFactory(UsuarioRepositorio usuarios) {
        super(usuarios);
    }

    @Override public TipoUsuario tipoSuportado() { return TipoUsuario.DOADOR; }

    @Override
    protected void validar(DadosUsuario dados) {
        super.validar(dados);
        obrigatorio(dados, "documento", "documento");
    }

    @Override
    protected Usuario criarUsuario(DadosUsuario dados, String senhaHash) {
        return new Doador(null, dados.nome().trim(), dados.email().trim().toLowerCase(), senhaHash, true,
                dados.atributo("documento").trim(), Boolean.parseBoolean(dados.atributo("pessoaJuridica")));
    }
}
