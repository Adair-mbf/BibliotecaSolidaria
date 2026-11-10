package br.com.bibliotecasolidaria.factory;

import br.com.bibliotecasolidaria.dto.DadosUsuario;
import br.com.bibliotecasolidaria.model.Doador;
import br.com.bibliotecasolidaria.model.Usuario;

public class DoadorFactory extends UsuarioFactory {
    @Override
    protected Usuario criarUsuario(DadosUsuario dados) {
        String doc = dados.getAtributosEspecificos() != null ? dados.getAtributosEspecificos().get("documento") : "";
        boolean pj = dados.getAtributosEspecificos() != null && Boolean.parseBoolean(dados.getAtributosEspecificos().get("pessoaJuridica"));
        return new Doador(null, dados.getNome(), dados.getEmail(), dados.getSenha(), true, doc, pj);
    }
}
