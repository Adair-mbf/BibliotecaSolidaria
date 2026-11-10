package br.com.bibliotecasolidaria.dto;

import br.com.bibliotecasolidaria.model.Domain.TipoUsuario;

import java.util.Map;

/** Dados de entrada para o cadastro desacoplado de um usuário. */
public record DadosUsuario(
        String nome,
        String email,
        String senha,
        TipoUsuario tipo,
        Map<String, String> atributosEspecificos) {

    public DadosUsuario {
        atributosEspecificos = atributosEspecificos == null
                ? Map.of()
                : Map.copyOf(atributosEspecificos);
    }

    public DadosUsuario(String nome, String email, String senha, TipoUsuario tipo) {
        this(nome, email, senha, tipo, Map.of());
    }

    public String atributoObrigatorio(String nomeAtributo) {
        String valor = atributosEspecificos.get(nomeAtributo);
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("O atributo específico '" + nomeAtributo + "' é obrigatório.");
        }
        return valor.trim();
    }

    public boolean atributoBooleano(String nomeAtributo) {
        String valor = atributoObrigatorio(nomeAtributo);
        if (!"true".equalsIgnoreCase(valor) && !"false".equalsIgnoreCase(valor)) {
            throw new IllegalArgumentException("O atributo '" + nomeAtributo + "' deve ser true ou false.");
        }
        return Boolean.parseBoolean(valor);
    }
}
