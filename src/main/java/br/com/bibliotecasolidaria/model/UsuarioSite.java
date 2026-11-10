package br.com.bibliotecasolidaria.model;

public class UsuarioSite extends Usuario {
    private String telefone;
    private String endereco;

    public UsuarioSite() { super(); }

    public UsuarioSite(Long id, String nome, String email, String senhaHash, boolean ativo, String telefone, String endereco) {
        super(id, nome, email, senhaHash, ativo);
        this.telefone = telefone;
        this.endereco = endereco;
    }

    public boolean podeEmprestar() {
        return isAtivo();
    }

    @Override
    public TipoUsuario getTipo() {
        return TipoUsuario.USUARIO_SITE;
    }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
}
