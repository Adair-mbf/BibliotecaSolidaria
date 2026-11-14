package br.com.bibliotecasolidaria.persistence;

import java.util.function.Supplier;

/** Executa um bloco de trabalho de forma atômica: tudo é confirmado ou tudo é desfeito. */
public interface ExecutorTransacional {
    <T> T emTransacao(Supplier<T> acao);

    default void emTransacao(Runnable acao) {
        emTransacao(() -> {
            acao.run();
            return null;
        });
    }
}
