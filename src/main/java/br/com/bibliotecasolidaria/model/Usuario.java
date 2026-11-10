package br.com.bibliotecasolidaria.model;

public abstract class Usuario {
    protected Long id;
    protected String nome;
    protected String email;
    protected String senhaHash;
    protected boolean ativo = true;

    public Usuario() {}

    public Usuario(Long id, String nome, String email, String senhaHash, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
        this.ativo = ativo;
    }

    public boolean autenticar(String senha) {
        return senha != null && (senha.equals(senhaHash) || String.valueOf(senha.hashCode()).equals(senhaHash));
    }

    public abstract TipoUsuario getTipo();

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
}
