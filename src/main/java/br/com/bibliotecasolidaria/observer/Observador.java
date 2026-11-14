package br.com.bibliotecasolidaria.observer;

/** Observer: reage a eventos publicados. */
public interface Observador {
    void atualizar(EventoBiblioteca evento);
}
