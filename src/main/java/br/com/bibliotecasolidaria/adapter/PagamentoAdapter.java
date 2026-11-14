package br.com.bibliotecasolidaria.adapter;

import br.com.bibliotecasolidaria.exception.NegocioException;
import br.com.bibliotecasolidaria.model.ComprovantePagamento;
import br.com.bibliotecasolidaria.model.Multa;
import br.com.bibliotecasolidaria.model.Usuario;
import br.com.bibliotecasolidaria.model.enums.StatusPagamento;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;

/** Adapter: traduz {@link GatewayPagamento} para as chamadas de {@link ApiPagamentoExterna}. */
public class PagamentoAdapter implements GatewayPagamento {
    private final ApiPagamentoExterna api;
    private final Clock clock;

    public PagamentoAdapter(ApiPagamentoExterna api, Clock clock) {
        this.api = api;
        this.clock = clock;
    }

    public PagamentoAdapter(ApiPagamentoExterna api) {
        this(api, Clock.systemDefaultZone());
    }

    @Override
    public ComprovantePagamento cobrar(Multa multa, Usuario pagador) {
        long centavos = multa.getValor().multiply(BigDecimal.valueOf(100)).longValueExact();
        String referencia;
        StatusPagamento status;
        try {
            referencia = api.criarCobranca(centavos, "Multa da biblioteca #" + multa.getId(), pagador.getEmail());
            status = traduzir(api.verificarCobranca(referencia));
        } catch (IllegalArgumentException e) {
            throw new NegocioException("O provedor de pagamento recusou a cobrança: " + e.getMessage(), e);
        }
        return new ComprovantePagamento(referencia, multa.getValor(), LocalDateTime.now(clock), status);
    }

    @Override
    public StatusPagamento consultarStatus(String idTransacao) {
        try {
            return traduzir(api.verificarCobranca(idTransacao));
        } catch (IllegalArgumentException e) {
            throw new NegocioException("Transação desconhecida: " + idTransacao, e);
        }
    }

    static StatusPagamento traduzir(String statusExterno) {
        return switch (statusExterno == null ? "" : statusExterno.toUpperCase()) {
            case "PAID" -> StatusPagamento.APROVADO;
            case "FAILED", "DECLINED" -> StatusPagamento.RECUSADO;
            case "REFUNDED" -> StatusPagamento.ESTORNADO;
            default -> StatusPagamento.PENDENTE; // PENDING ou desconhecido: nunca aprova por engano
        };
    }
}
