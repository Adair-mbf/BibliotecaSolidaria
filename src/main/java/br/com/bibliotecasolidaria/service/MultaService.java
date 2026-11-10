package br.com.bibliotecasolidaria.service;

import br.com.bibliotecasolidaria.adapter.GatewayPagamento;
import br.com.bibliotecasolidaria.model.*;
import br.com.bibliotecasolidaria.repository.MultaRepositorio;
import br.com.bibliotecasolidaria.repository.UsuarioRepositorio;
import br.com.bibliotecasolidaria.strategy.CalculoMultaStrategy;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class MultaService {
    private final MultaRepositorio multaRepo;
    private final UsuarioRepositorio usuarioRepo;
    private CalculoMultaStrategy estrategia;
    private final GatewayPagamento gateway;

    public MultaService(MultaRepositorio mr, UsuarioRepositorio ur, CalculoMultaStrategy estrategia, GatewayPagamento gateway) {
        this.multaRepo = mr;
        this.usuarioRepo = ur;
        this.estrategia = estrategia;
        this.gateway = gateway;
    }

    public void definirEstrategia(CalculoMultaStrategy e) {
        this.estrategia = e;
    }

    public Multa gerarMulta(Emprestimo emprestimo) {
        int dias = emprestimo.diasAtraso();
        BigDecimal valor = estrategia.calcular(dias, emprestimo);
        Multa m = new Multa(null, emprestimo.getId(), emprestimo.getUsuarioId(), valor, LocalDate.now(), null, StatusMulta.PENDENTE);
        return multaRepo.salvar(m);
    }

    public List<Multa> listarPendentes(Long idUsuario) {
        return multaRepo.listarPendentes(idUsuario);
    }

    public ComprovantePagamento pagarMulta(Long idMulta) {
        Multa multa = multaRepo.buscarPorId(idMulta)
                .orElseThrow(() -> new IllegalArgumentException("Multa não encontrada."));
        Usuario user = usuarioRepo.buscarPorId(multa.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        if (!(user instanceof UsuarioSite pagador)) {
            throw new IllegalStateException("Usuário não é apto para pagamento de multa.");
        }

        ComprovantePagamento comprovante = gateway.cobrar(multa, pagador);
        if (comprovante.getStatus() == StatusPagamento.APROVADO) {
            multa.quitar();
            multaRepo.salvar(multa);
        }
        return comprovante;
    }
}
