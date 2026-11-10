package br.com.bibliotecasolidaria.dto;

import br.com.bibliotecasolidaria.model.TipoUsuario;
import java.util.Map;

public class DadosUsuario {
    private String nome;
    private String email;
    private String senha;
    private TipoUsuario tipo;
    private Map<String, String> atributosEspecificos;

    public DadosUsuario() {}

    public DadosUsuario(String nome, String email, String senha, TipoUsuario tipo, Map<String, String> atributosEspecificos) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.tipo = tipo;
        this.atributosEspecificos = atributosEspecificos;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public TipoUsuario getTipo() { return tipo; }
    public void setTipo(TipoUsuario tipo) { this.tipo = tipo; }
    public Map<String, String> getAtributosEspecificos() { return atributosEspecificos; }
    public void setAtributosEspecificos(Map<String, String> atributosEspecificos) { this.atributosEspecificos = atributosEspecificos; }
}
