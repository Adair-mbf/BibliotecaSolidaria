package br.com.bibliotecasolidaria.observer;

import br.com.bibliotecasolidaria.model.TipoEvento;

public interface Sujeito {
    void registrar(Observador o, TipoEvento tipo);
    void remover(Observador o, TipoEvento tipo);
    void notificar(EventoBiblioteca evento);
}
