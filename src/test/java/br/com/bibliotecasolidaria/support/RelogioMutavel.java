package br.com.bibliotecasolidaria.support;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

/** Relógio de teste: começa em 2026-11-10T12:00Z e pode ser avançado. */
public class RelogioMutavel extends Clock {
    private Instant agora = Instant.parse("2026-11-10T12:00:00Z");

    public void avancarDias(long dias) {
        agora = agora.plusSeconds(dias * 86_400);
    }

    @Override public ZoneId getZone() { return ZoneOffset.UTC; }

    @Override public Clock withZone(ZoneId zone) { return this; }

    @Override public Instant instant() { return agora; }
}
