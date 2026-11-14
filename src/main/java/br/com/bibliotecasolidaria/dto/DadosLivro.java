package br.com.bibliotecasolidaria.dto;

import java.util.List;

/** Metadados de um livro vindos do acervo global. */
public record DadosLivro(String isbn, String titulo, List<String> autores, String editora, int anoPublicacao,
                         String capaUrl) {
    public String autoresComoTexto() {
        return String.join(", ", autores);
    }
}
