package br.com.bibliotecasolidaria.adapter;

import br.com.bibliotecasolidaria.dto.DadosLivro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Adapter: converte as respostas textuais de {@link ApiAcervoGlobal} em {@link DadosLivro}. */
public class AcervoGlobalAdapter implements AcervoGlobalProvider {
    private final ApiAcervoGlobal api;

    public AcervoGlobalAdapter(ApiAcervoGlobal api) {
        this.api = api;
    }

    @Override
    public Optional<DadosLivro> buscarPorIsbn(String isbn) {
        if (isbn == null || isbn.isBlank()) {
            return Optional.empty();
        }
        return converter(api.consultarISBN(isbn.replaceAll("[\\s-]", "")));
    }

    @Override
    public List<DadosLivro> pesquisar(String termo, int pagina) {
        List<DadosLivro> resultado = new ArrayList<>();
        for (String linha : api.pesquisar(termo, pagina).split("\n")) {
            converter(linha).ifPresent(resultado::add);
        }
        return resultado;
    }

    private static Optional<DadosLivro> converter(String registro) {
        if (registro == null || registro.isBlank()) {
            return Optional.empty();
        }
        Map<String, String> campos = new HashMap<>();
        for (String par : registro.split(";")) {
            int i = par.indexOf('=');
            if (i > 0) {
                campos.put(par.substring(0, i).trim(), par.substring(i + 1).trim());
            }
        }
        if (!campos.containsKey("isbn") || !campos.containsKey("titulo")) {
            return Optional.empty();
        }
        int ano;
        try {
            ano = Integer.parseInt(campos.getOrDefault("ano", "0"));
        } catch (NumberFormatException e) {
            ano = 0;
        }
        List<String> autores = Arrays.stream(campos.getOrDefault("autores", "").split("\\|"))
                .map(String::trim).filter(a -> !a.isEmpty()).toList();
        return Optional.of(new DadosLivro(campos.get("isbn"), campos.get("titulo"), autores,
                campos.get("editora"), ano, campos.get("capa")));
    }
}
