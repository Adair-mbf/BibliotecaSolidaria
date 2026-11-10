package br.com.bibliotecasolidaria.factory;

import br.com.bibliotecasolidaria.dto.DadosUsuario;
import br.com.bibliotecasolidaria.model.Usuario;
import br.com.bibliotecasolidaria.model.UsuarioSite;

public class UsuarioSiteFactory extends UsuarioFactory {
    @Override
    protected Usuario criarUsuario(DadosUsuario dados) {
        String tel = dados.getAtributosEspecificos() != null ? dados.getAtributosEspecificos().get("telefone") : "";
        String end = dados.getAtributosEspecificos() != null ? dados.getAtributosEspecificos().get("endereco") : "";
        return new UsuarioSite(null, dados.getNome(), dados.getEmail(), dados.getSenha(), true, tel, end);
    }
}
