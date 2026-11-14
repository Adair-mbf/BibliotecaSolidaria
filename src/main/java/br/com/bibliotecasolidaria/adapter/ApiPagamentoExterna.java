package br.com.bibliotecasolidaria.adapter;

import java.util.HashMap;
import java.util.Map;

/**
 * Adaptee: API de pagamento de terceiros, com interface incompatível (centavos e códigos em inglês).
 * Esta implementação é uma <b>simulação em memória</b> que aprova toda cobrança válida; substitua-a
 * (ou herde dela) por um cliente HTTP real do provedor escolhido.
 */
public class ApiPagamentoExterna {
    private final Map<String, String> cobrancas = new HashMap<>();
    private long sequencia = 0;

    /** Cria a cobrança e devolve a referência. */
    public synchronized String criarCobranca(long valorCentavos, String descricao, String emailPagador) {
        if (valorCentavos <= 0 || emailPagador == null || emailPagador.isBlank()) {
            throw new IllegalArgumentException("Cobrança inválida.");
        }
        String ref = "TX-" + (++sequencia);
        cobrancas.put(ref, "PAID");
        return ref;
    }

    /** Retorna PAID, PENDING, FAILED ou REFUNDED. */
    public synchronized String verificarCobranca(String referencia) {
        String status = cobrancas.get(referencia);
        if (status == null) {
            throw new IllegalArgumentException("Cobrança desconhecida: " + referencia);
        }
        return status;
    }
}
