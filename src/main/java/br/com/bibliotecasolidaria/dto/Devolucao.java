package br.com.bibliotecasolidaria.dto;

import br.com.bibliotecasolidaria.model.Emprestimo;
import br.com.bibliotecasolidaria.model.Multa;

/** Resultado de uma devolução; {@code multa} é nula quando não houve atraso. */
public record Devolucao(Emprestimo emprestimo, Multa multa, int diasAtraso) {
    public boolean geroumulta() {
        return multa != null;
    }

    @Override
    public String toString() {
        return "Devolucao{" + emprestimo + ", diasAtraso=" + diasAtraso
                + (multa == null ? ", sem multa" : ", " + multa) + "}";
    }
}
