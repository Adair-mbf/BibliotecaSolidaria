package br.com.bibliotecasolidaria.model;

import br.com.bibliotecasolidaria.model.enums.TipoUsuario;

/** Funcionário da biblioteca. */
public class Funcionario extends Usuario {
    private String matricula;
    private String cargo;

    public Funcionario(Long id, String nome, String email, String senhaHash, boolean ativo, String matricula, String cargo) {
        super(id, nome, email, senhaHash, ativo);
        this.matricula = matricula;
        this.cargo = cargo;
    }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }
    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    @Override
    public TipoUsuario getTipo() { return TipoUsuario.FUNCIONARIO; }

    @Override
    public String toString() {
        return "Funcionario{" + "id=" + getId() + ", " + "nome='" + getNome() + "'" + ", " + "email='" + getEmail() + "'" + ", " + "ativo=" + isAtivo() + ", " + "matricula='" + getMatricula() + "'" + ", " + "cargo='" + getCargo() + "'" + "}";
    }
}
