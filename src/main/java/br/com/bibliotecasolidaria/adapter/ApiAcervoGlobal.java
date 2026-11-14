package br.com.bibliotecasolidaria.adapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptee: serviço externo de catálogo que responde em texto {@code chave=valor;...} (autores separados por
 * {@code |}). Esta é uma <b>simulação em memória</b>; substitua por um cliente HTTP do serviço real.
 */
public class ApiAcervoGlobal {
    static final int TAMANHO_PAGINA = 5;

    private final List<String> registros = new ArrayList<>();

    public ApiAcervoGlobal() {
        registros.add("isbn=9780000000011;titulo=Dom Casmurro;autores=Machado de Assis;editora=Editora Simulada;ano=1899;capa=https://exemplo.invalid/capas/1.jpg");
        registros.add("isbn=9780000000028;titulo=O Alienista;autores=Machado de Assis;editora=Editora Simulada;ano=1882;capa=https://exemplo.invalid/capas/2.jpg");
        registros.add("isbn=9780000000035;titulo=Memórias Póstumas de Brás Cubas;autores=Machado de Assis;editora=Editora Simulada;ano=1881;capa=https://exemplo.invalid/capas/3.jpg");
    }

    public ApiAcervoGlobal(List<String> registros) {
        this.registros.addAll(registros);
    }

    /** Devolve o registro do ISBN ou texto vazio se não existir. */
    public String consultarISBN(String isbn) {
        return registros.stream().filter(r -> r.contains("isbn=" + isbn + ";")).findFirst().orElse("");
    }

    /** Devolve até 5 registros (um por linha) cujo texto contenha o termo; vazio além da última página. */
    public String pesquisar(String termo, int pagina) {
        String t = termo == null ? "" : termo.toLowerCase();
        List<String> achados = registros.stream().filter(r -> r.toLowerCase().contains(t)).toList();
        int de = Math.max(0, pagina - 1) * TAMANHO_PAGINA;
        if (de >= achados.size()) {
            return "";
        }
        return String.join("\n", achados.subList(de, Math.min(achados.size(), de + TAMANHO_PAGINA)));
    }
}
