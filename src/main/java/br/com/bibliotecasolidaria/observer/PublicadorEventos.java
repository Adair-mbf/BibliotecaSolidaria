package br.com.bibliotecasolidaria.observer;

import br.com.bibliotecasolidaria.model.enums.TipoEvento;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Publicador de eventos: entrega cada evento aos observadores do seu tipo, isolando falhas entre eles. */
public class PublicadorEventos implements Sujeito {
    private static final Logger LOG = Logger.getLogger(PublicadorEventos.class.getName());

    private final Map<TipoEvento, List<Observador>> observadores = new EnumMap<>(TipoEvento.class);

    @Override
    public synchronized void registrar(TipoEvento tipo, Observador observador) {
        observadores.computeIfAbsent(tipo, t -> new CopyOnWriteArrayList<>()).add(observador);
    }

    @Override
    public synchronized void remover(TipoEvento tipo, Observador observador) {
        List<Observador> lista = observadores.get(tipo);
        if (lista != null) {
            lista.remove(observador);
        }
    }

    @Override
    public void notificar(EventoBiblioteca evento) {
        List<Observador> lista;
        synchronized (this) {
            lista = observadores.getOrDefault(evento.tipo(), List.of());
        }
        for (Observador o : lista) {
            try {
                o.atualizar(evento);
            } catch (RuntimeException e) {
                LOG.log(Level.WARNING, "Observador " + o.getClass().getSimpleName()
                        + " falhou ao tratar " + evento.tipo(), e);
            }
        }
    }
}
