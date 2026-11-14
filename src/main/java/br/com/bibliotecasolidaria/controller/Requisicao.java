package br.com.bibliotecasolidaria.controller;

import br.com.bibliotecasolidaria.exception.ValidacaoException;
import br.com.bibliotecasolidaria.util.Textos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

/** Parâmetros de uma operação (nome do campo → texto), com leitura tipada e validação. */
public final class Requisicao {
    private final Map<String, String> campos;

    public Requisicao(Map<String, String> campos) {
        this.campos = new HashMap<>(campos);
    }

    /** Atalho: {@code Requisicao.de("id", "1", "nome", "Ana")}. */
    public static Requisicao de(String... paresChaveValor) {
        if (paresChaveValor.length % 2 != 0) {
            throw new IllegalArgumentException("Informe pares chave/valor.");
        }
        Map<String, String> m = new HashMap<>();
        for (int i = 0; i < paresChaveValor.length; i += 2) {
            m.put(paresChaveValor[i], paresChaveValor[i + 1]);
        }
        return new Requisicao(m);
    }

    public Map<String, String> todos() { return Map.copyOf(campos); }

    public boolean tem(String chave) { return !Textos.emBranco(campos.get(chave)); }

    public String texto(String chave) {
        String v = Textos.normalizar(campos.get(chave));
        if (v == null) throw new ValidacaoException("O campo '" + chave + "' é obrigatório.");
        return v;
    }

    public String textoOpcional(String chave) { return Textos.normalizar(campos.get(chave)); }

    public long longo(String chave) {
        try {
            return Long.parseLong(texto(chave));
        } catch (NumberFormatException e) {
            throw new ValidacaoException("O campo '" + chave + "' deve ser um número inteiro.");
        }
    }

    public Long longoOpcional(String chave) { return tem(chave) ? longo(chave) : null; }

    public int inteiro(String chave) {
        try {
            return Integer.parseInt(texto(chave));
        } catch (NumberFormatException e) {
            throw new ValidacaoException("O campo '" + chave + "' deve ser um número inteiro.");
        }
    }

    public Integer inteiroOpcional(String chave) { return tem(chave) ? inteiro(chave) : null; }

    public BigDecimal decimal(String chave) {
        try {
            return new BigDecimal(texto(chave).replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new ValidacaoException("O campo '" + chave + "' deve ser um número decimal.");
        }
    }

    public LocalDate data(String chave) {
        try {
            return LocalDate.parse(texto(chave));
        } catch (DateTimeParseException e) {
            throw new ValidacaoException("O campo '" + chave + "' deve estar no formato AAAA-MM-DD.");
        }
    }

    public boolean booleano(String chave) {
        String v = texto(chave).toLowerCase();
        if (v.equals("true") || v.equals("sim") || v.equals("s")) return true;
        if (v.equals("false") || v.equals("nao") || v.equals("não") || v.equals("n")) return false;
        throw new ValidacaoException("O campo '" + chave + "' deve ser true/false (ou sim/não).");
    }

    public Boolean booleanoOpcional(String chave) { return tem(chave) ? booleano(chave) : null; }

    public <E extends Enum<E>> E enumeracao(String chave, Class<E> tipo) {
        String v = texto(chave);
        try {
            return Enum.valueOf(tipo, v.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidacaoException("Valor inválido para '" + chave + "': " + v + ". Opções: "
                    + java.util.Arrays.toString(tipo.getEnumConstants()));
        }
    }

    public <E extends Enum<E>> E enumeracaoOpcional(String chave, Class<E> tipo) {
        return tem(chave) ? enumeracao(chave, tipo) : null;
    }
}
