package br.com.bibliotecasolidaria.adapter;

import br.com.bibliotecasolidaria.dto.DadosLivro;
import java.util.List;
import java.util.Optional;

public interface AcervoGlobalProvider {
    Optional<DadosLivro> buscarPorIsbn(String isbn);
    List<DadosLivro> buscarPorTitulo(String titulo);
}
