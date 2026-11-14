package br.com.bibliotecasolidaria.view;

import java.util.Arrays;

/** Campo solicitado ao usuário; o nome é a chave usada na {@code Requisicao}. */
public record Campo(String nome, boolean obrigatorio, String dica) {
    public static Campo obrigatorio(String nome) { return new Campo(nome, true, ""); }

    public static Campo opcional(String nome) { return new Campo(nome, false, ""); }

    public static Campo data(String nome, boolean obrigatorio) { return new Campo(nome, obrigatorio, "AAAA-MM-DD"); }

    public static Campo booleano(String nome, boolean obrigatorio) { return new Campo(nome, obrigatorio, "true/false"); }

    public static <E extends Enum<E>> Campo enumerado(String nome, Class<E> tipo, boolean obrigatorio) {
        return new Campo(nome, obrigatorio, String.join("|", Arrays.stream(tipo.getEnumConstants()).map(Enum::name).toList()));
    }

    String rotulo() {
        return nome + (dica.isEmpty() ? "" : " [" + dica + "]") + (obrigatorio ? "" : " (opcional)") + ": ";
    }
}
