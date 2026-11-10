package br.com.bibliotecasolidaria.controller;

import br.com.bibliotecasolidaria.dto.Devolucao;
import br.com.bibliotecasolidaria.facade.EmprestimoFacade;
import br.com.bibliotecasolidaria.model.ComprovantePagamento;
import br.com.bibliotecasolidaria.model.Emprestimo;

public class EmprestimoController {
    private final EmprestimoFacade facade;

    public EmprestimoController(EmprestimoFacade facade) {
        this.facade = facade;
    }

    public record Resposta<T>(int status, String mensagem, T dados) {}

    public Resposta<Emprestimo> emprestar(Long usuarioId, Long itemId) {
        try {
            Emprestimo emp = facade.realizarEmprestimo(usuarioId, itemId);
            return new Resposta<>(201, "Empréstimo realizado com sucesso", emp);
        } catch (Exception e) {
            return new Resposta<>(400, e.getMessage(), null);
        }
    }

    public Resposta<Devolucao> devolver(Long idEmprestimo) {
        try {
            Devolucao dev = facade.devolver(idEmprestimo);
            return new Resposta<>(200, "Devolução concluída", dev);
        } catch (Exception e) {
            return new Resposta<>(400, e.getMessage(), null);
        }
    }

    public Resposta<Emprestimo> renovar(Long idEmprestimo) {
        try {
            Emprestimo emp = facade.renovar(idEmprestimo);
            return new Resposta<>(200, "Renovação efetuada", emp);
        } catch (Exception e) {
            return new Resposta<>(400, e.getMessage(), null);
        }
    }

    public Resposta<ComprovantePagamento> pagarMulta(Long idMulta) {
        try {
            ComprovantePagamento cp = facade.pagarMulta(idMulta);
            return new Resposta<>(200, "Multa quitada com sucesso", cp);
        } catch (Exception e) {
            return new Resposta<>(400, e.getMessage(), null);
        }
    }
}
