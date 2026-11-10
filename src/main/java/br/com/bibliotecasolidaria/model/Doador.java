package br.com.bibliotecasolidaria.model;

public class Doador extends Usuario {
    private String documento;
    private boolean pessoaJuridica;

    public Doador() { super(); }

    public Doador(Long id, String nome, String email, String senhaHash, boolean ativo, String documento, boolean pessoaJuridica) {
        super(id, nome, email, senhaHash, ativo);
        this.documento = documento;
        this.pessoaJuridica = pessoaJuridica;
    }

    @Override
    public TipoUsuario getTipo() {
        return TipoUsuario.DOADOR;
    }

    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }
    public boolean isPessoaJuridica() { return pessoaJuridica; }
    public void setPessoaJuridica(boolean pessoaJuridica) { this.pessoaJuridica = pessoaJuridica; }
}
