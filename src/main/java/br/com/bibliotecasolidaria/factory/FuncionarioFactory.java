package br.com.bibliotecasolidaria.factory;

import br.com.bibliotecasolidaria.dto.DadosUsuario;
import br.com.bibliotecasolidaria.model.Funcionario;
import br.com.bibliotecasolidaria.model.Usuario;
import br.com.bibliotecasolidaria.model.enums.TipoUsuario;
import br.com.bibliotecasolidaria.persistence.UsuarioRepositorio;

/** Atributos: matricula e cargo (obrigatórios). */
public class FuncionarioFactory extends UsuarioFactory {
    public FuncionarioFactory(UsuarioRepositorio usuarios) {
        super(usuarios);
    }

    @Override public TipoUsuario tipoSuportado() { return TipoUsuario.FUNCIONARIO; }

    @Override
    protected void validar(DadosUsuario dados) {
        super.validar(dados);
        obrigatorio(dados, "matricula", "matrícula");
        obrigatorio(dados, "cargo", "cargo");
    }

    @Override
    protected Usuario criarUsuario(DadosUsuario dados, String senhaHash) {
        return new Funcionario(null, dados.nome().trim(), dados.email().trim().toLowerCase(), senhaHash, true,
                dados.atributo("matricula").trim(), dados.atributo("cargo").trim());
    }
}
