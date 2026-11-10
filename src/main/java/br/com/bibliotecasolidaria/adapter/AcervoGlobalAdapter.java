package br.com.bibliotecasolidaria.adapter;

import br.com.bibliotecasolidaria.dto.DadosLivro;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AcervoGlobalAdapter implements AcervoGlobalProvider {
    @Override
    public Optional<DadosLivro> buscarPorIsbn(String isbn) {
        return Optional.of(new DadosLivro("Livro Externo", List.of("Autor Teste"), isbn, "Editora Global", 2024, "http://capa.url"));
    }

    @Override
    public List<DadosLivro> buscarPorTitulo(String titulo) {
        List<DadosLivro> lista = new ArrayList<>();
        lista.add(new DadosLivro(titulo, List.of("Autor Global"), "1234567890", "Editora Global", 2024, "http://capa.url"));
        return lista;
    }
}
