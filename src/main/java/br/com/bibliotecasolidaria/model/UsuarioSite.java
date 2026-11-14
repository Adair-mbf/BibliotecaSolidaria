package br.com.bibliotecasolidaria.model;

import br.com.bibliotecasolidaria.model.enums.TipoUsuario;

/** Usuário do site, o único perfil que pode tomar empréstimos e fazer reservas. */
public class UsuarioSite extends Usuario {
    private String telefone;
    private String endereco;

    public UsuarioSite(Long id, String nome, String email, String senhaHash, boolean ativo, String telefone, String endereco) {
        super(id, nome, email, senhaHash, ativo);
        this.telefone = telefone;
        this.endereco = endereco;
    }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public boolean podeEmprestar() { return isAtivo(); }

    @Override
    public TipoUsuario getTipo() { return TipoUsuario.USUARIO_SITE; }

    @Override
    public String toString() {
        return "UsuarioSite{" + "id=" + getId() + ", " + "nome='" + getNome() + "'" + ", " + "email='" + getEmail() + "'" + ", " + "ativo=" + isAtivo() + ", " + "telefone='" + getTelefone() + "'" + ", " + "endereco='" + getEndereco() + "'" + "}";
    }
}
