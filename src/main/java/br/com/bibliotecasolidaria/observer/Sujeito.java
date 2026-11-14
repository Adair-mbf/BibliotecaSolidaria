package br.com.bibliotecasolidaria.observer;

import br.com.bibliotecasolidaria.model.enums.TipoEvento;

/** Subject do padrão Observer. */
public interface Sujeito {
    void registrar(TipoEvento tipo, Observador observador);

    void remover(TipoEvento tipo, Observador observador);

    void notificar(EventoBiblioteca evento);
}
