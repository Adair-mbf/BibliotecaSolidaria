package br.com.bibliotecasolidaria.adapter;

import br.com.bibliotecasolidaria.dto.DadosLivro;

import java.util.List;
import java.util.Optional;

/** Target do Adapter: consulta a um catálogo bibliográfico global. */
public interface AcervoGlobalProvider {
    Optional<DadosLivro> buscarPorIsbn(String isbn);

    List<DadosLivro> pesquisar(String termo, int pagina);
}
