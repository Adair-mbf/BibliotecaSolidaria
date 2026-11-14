package br.com.bibliotecasolidaria.model;

import br.com.bibliotecasolidaria.util.SenhaUtil;

import br.com.bibliotecasolidaria.model.enums.TipoUsuario;

/** Usuário do sistema (classe base de Doador, UsuarioSite e Funcionario). */
public abstract class Usuario {
    private Long id;
    private String nome;
    private String email;
    private String senhaHash;
    private boolean ativo;

    protected Usuario(Long id, String nome, String email, String senhaHash, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
        this.ativo = ativo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    /** Confere a senha em texto puro contra o hash armazenado. */
    public boolean autenticar(String senha) {
        return senha != null && senhaHash != null && SenhaUtil.verificar(senha, senhaHash);
    }

    public abstract TipoUsuario getTipo();

    @Override
    public String toString() {
        return "Usuario{" + "id=" + getId() + ", " + "nome='" + getNome() + "'" + ", " + "email='" + getEmail() + "'" + ", " + "ativo=" + isAtivo() + "}";
    }
}
