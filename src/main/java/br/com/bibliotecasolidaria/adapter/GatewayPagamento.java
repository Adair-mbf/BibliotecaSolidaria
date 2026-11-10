package br.com.bibliotecasolidaria.adapter;

import br.com.bibliotecasolidaria.model.ComprovantePagamento;
import br.com.bibliotecasolidaria.model.Multa;
import br.com.bibliotecasolidaria.model.StatusPagamento;
import br.com.bibliotecasolidaria.model.UsuarioSite;

public interface GatewayPagamento {
    ComprovantePagamento cobrar(Multa multa, UsuarioSite pagador);
    StatusPagamento consultarStatus(String idTransacao);
}
