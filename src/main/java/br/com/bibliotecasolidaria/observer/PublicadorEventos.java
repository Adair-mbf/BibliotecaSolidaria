package br.com.bibliotecasolidaria.observer;

import br.com.bibliotecasolidaria.model.TipoEvento;
import java.util.*;

public class PublicadorEventos implements Sujeito {
    private final Map<TipoEvento, List<Observador>> observadores = new EnumMap<>(TipoEvento.class);

    public PublicadorEventos() {
        for (TipoEvento t : TipoEvento.values()) {
            observadores.put(t, new ArrayList<>());
        }
    }

    @Override
    public void registrar(Observador o, TipoEvento tipo) {
        observadores.computeIfAbsent(tipo, k -> new ArrayList<>()).add(o);
    }

    @Override
    public void remover(Observador o, TipoEvento tipo) {
        List<Observador> list = observadores.get(tipo);
        if (list != null) {
            list.remove(o);
        }
    }

    @Override
    public void notificar(EventoBiblioteca evento) {
        if (evento == null || evento.getTipo() == null) return;
        List<Observador> list = observadores.get(evento.getTipo());
        if (list != null) {
            for (Observador obs : list) {
                obs.atualizar(evento);
            }
        }
    }
}
