package br.com.bibliotecasolidaria.dto;

import br.com.bibliotecasolidaria.model.ItemAcervo;
import br.com.bibliotecasolidaria.model.Livro;

/** Item disponível junto com os dados da obra, para consultas ao acervo. */
public record ItemConsultado(Livro livro, ItemAcervo item) {
    @Override
    public String toString() {
        return "Item #" + item.getId() + " [" + item.getTipo() + ", " + item.getStatus() + "] - \""
                + livro.getTitulo() + "\" de " + livro.getAutor() + " (" + livro.getCategoria() + ")";
    }
}
