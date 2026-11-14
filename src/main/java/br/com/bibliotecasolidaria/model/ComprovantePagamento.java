package br.com.bibliotecasolidaria.model;

import br.com.bibliotecasolidaria.model.enums.StatusPagamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Comprovante devolvido pelo gateway de pagamento. */
public record ComprovantePagamento(String idTransacao, BigDecimal valor, LocalDateTime dataPagamento,
                                   StatusPagamento status) {
    public boolean aprovado() {
        return status == StatusPagamento.APROVADO;
    }
}
