package br.com.bibliotecasolidaria.adapter;

import br.com.bibliotecasolidaria.model.ComprovantePagamento;
import br.com.bibliotecasolidaria.model.Multa;
import br.com.bibliotecasolidaria.model.StatusPagamento;
import br.com.bibliotecasolidaria.model.UsuarioSite;
import java.time.LocalDateTime;
import java.util.UUID;

public class PagamentoAdapter implements GatewayPagamento {
    @Override
    public ComprovantePagamento cobrar(Multa multa, UsuarioSite pagador) {
        return new ComprovantePagamento(
            UUID.randomUUID().toString(),
            multa.getValor(),
            LocalDateTime.now(),
            StatusPagamento.APROVADO
        );
    }

    @Override
    public StatusPagamento consultarStatus(String idTransacao) {
        return StatusPagamento.APROVADO;
    }
}
