package br.com.bibliotecasolidaria.model;

import br.com.bibliotecasolidaria.model.enums.TipoUsuario;

/** Pessoa física ou jurídica que realiza doações. */
public class Doador extends Usuario {
    private String documento;
    private boolean pessoaJuridica;

    public Doador(Long id, String nome, String email, String senhaHash, boolean ativo, String documento, boolean pessoaJuridica) {
        super(id, nome, email, senhaHash, ativo);
        this.documento = documento;
        this.pessoaJuridica = pessoaJuridica;
    }

    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }
    public boolean isPessoaJuridica() { return pessoaJuridica; }
    public void setPessoaJuridica(boolean pessoaJuridica) { this.pessoaJuridica = pessoaJuridica; }

    @Override
    public TipoUsuario getTipo() { return TipoUsuario.DOADOR; }

    @Override
    public String toString() {
        return "Doador{" + "id=" + getId() + ", " + "nome='" + getNome() + "'" + ", " + "email='" + getEmail() + "'" + ", " + "ativo=" + isAtivo() + ", " + "documento='" + getDocumento() + "'" + ", " + "pessoaJuridica=" + isPessoaJuridica() + "}";
    }
}
