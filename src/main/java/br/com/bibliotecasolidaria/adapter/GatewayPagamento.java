package br.com.bibliotecasolidaria.adapter;

import br.com.bibliotecasolidaria.model.ComprovantePagamento;
import br.com.bibliotecasolidaria.model.Multa;
import br.com.bibliotecasolidaria.model.Usuario;
import br.com.bibliotecasolidaria.model.enums.StatusPagamento;

/** Target do Adapter: interface de pagamento usada pelo sistema. */
public interface GatewayPagamento {
    ComprovantePagamento cobrar(Multa multa, Usuario pagador);

    StatusPagamento consultarStatus(String idTransacao);
}
