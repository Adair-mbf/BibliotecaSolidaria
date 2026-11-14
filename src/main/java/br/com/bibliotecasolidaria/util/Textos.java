package br.com.bibliotecasolidaria.util;

/** Utilitários de texto. */
public final class Textos {
    private Textos() { }

    public static boolean emBranco(String valor) {
        return valor == null || valor.isBlank();
    }

    /** Retorna o texto sem espaços nas pontas, ou {@code null} se estiver vazio. */
    public static String normalizar(String valor) {
        return emBranco(valor) ? null : valor.trim();
    }
}
