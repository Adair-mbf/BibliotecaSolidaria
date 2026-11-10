package br.com.bibliotecasolidaria.factory;

import br.com.bibliotecasolidaria.dto.DadosUsuario;
import br.com.bibliotecasolidaria.model.Funcionario;
import br.com.bibliotecasolidaria.model.Usuario;

public class FuncionarioFactory extends UsuarioFactory {
    @Override
    protected Usuario criarUsuario(DadosUsuario dados) {
        String mat = dados.getAtributosEspecificos() != null ? dados.getAtributosEspecificos().get("matricula") : "";
        String cargo = dados.getAtributosEspecificos() != null ? dados.getAtributosEspecificos().get("cargo") : "Atendente";
        return new Funcionario(null, dados.getNome(), dados.getEmail(), dados.getSenha(), true, mat, cargo);
    }
}
