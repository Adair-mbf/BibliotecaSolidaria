package br.com.bibliotecasolidaria.observer;

import br.com.bibliotecasolidaria.model.Usuario;
import br.com.bibliotecasolidaria.model.enums.TipoEvento;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Evento de domínio publicado pelos serviços. {@code mensagem} é opcional; sem ela o observador a compõe. */
public record EventoBiblioteca(TipoEvento tipo, Usuario destinatario, String mensagem, Map<String, Object> dados,
                               LocalDateTime ocorridoEm) {
    public EventoBiblioteca {
        dados = dados == null ? Map.of() : Collections.unmodifiableMap(new LinkedHashMap<>(dados));
    }

    public EventoBiblioteca(TipoEvento tipo, Usuario destinatario, Map<String, Object> dados,
                            LocalDateTime ocorridoEm) {
        this(tipo, destinatario, null, dados, ocorridoEm);
    }
}
