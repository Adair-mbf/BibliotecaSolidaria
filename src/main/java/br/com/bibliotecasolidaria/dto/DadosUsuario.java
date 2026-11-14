package br.com.bibliotecasolidaria.dto;

import br.com.bibliotecasolidaria.model.enums.TipoUsuario;

import java.util.Map;

/** Dados de cadastro ou atualização de usuário; {@code atributos} guarda os campos específicos do tipo. */
public record DadosUsuario(String nome, String email, String senha, TipoUsuario tipo, Map<String, String> atributos) {
    public DadosUsuario {
        atributos = atributos == null ? Map.of() : Map.copyOf(atributos);
    }

    public String atributo(String chave) {
        return atributos.get(chave);
    }
}
