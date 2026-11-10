package br.com.bibliotecasolidaria.dto;

import br.com.bibliotecasolidaria.model.Emprestimo;
import br.com.bibliotecasolidaria.model.Multa;

public class Devolucao {
    private Emprestimo emprestimo;
    private Multa multa;
    private int atrasoEmDias;

    public Devolucao() {}

    public Devolucao(Emprestimo emprestimo, Multa multa, int atrasoEmDias) {
        this.emprestimo = emprestimo;
        this.multa = multa;
        this.atrasoEmDias = atrasoEmDias;
    }

    public Emprestimo getEmprestimo() { return emprestimo; }
    public void setEmprestimo(Emprestimo emprestimo) { this.emprestimo = emprestimo; }
    public Multa getMulta() { return multa; }
    public void setMulta(Multa multa) { this.multa = multa; }
    public int getAtrasoEmDias() { return atrasoEmDias; }
    public void setAtrasoEmDias(int atrasoEmDias) { this.atrasoEmDias = atrasoEmDias; }
}
