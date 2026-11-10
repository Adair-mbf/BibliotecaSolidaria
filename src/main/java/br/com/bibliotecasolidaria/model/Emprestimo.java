package br.com.bibliotecasolidaria.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Emprestimo {
    private Long id;
    private Long usuarioId;
    private Long itemAcervoId;
    private LocalDate dataEmprestimo;
    private LocalDate dataPrevistaDevolucao;
    private LocalDate dataDevolucao;
    private int renovacoes;
    private StatusEmprestimo status;

    public Emprestimo() {}

    public Emprestimo(Long id, Long usuarioId, Long itemAcervoId, LocalDate dataEmprestimo, LocalDate dataPrevistaDevolucao, LocalDate dataDevolucao, int renovacoes, StatusEmprestimo status) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.itemAcervoId = itemAcervoId;
        this.dataEmprestimo = dataEmprestimo;
        this.dataPrevistaDevolucao = dataPrevistaDevolucao;
        this.dataDevolucao = dataDevolucao;
        this.renovacoes = renovacoes;
        this.status = status != null ? status : StatusEmprestimo.ATIVO;
    }

    public void renovar(LocalDate novoPrazo) {
        this.dataPrevistaDevolucao = novoPrazo;
        this.renovacoes++;
    }

    public void registrarDevolucao() {
        this.dataDevolucao = LocalDate.now();
        this.status = StatusEmprestimo.DEVOLVIDO;
    }

    public int diasAtraso() {
        LocalDate dataFinal = (dataDevolucao != null) ? dataDevolucao : LocalDate.now();
        if (dataPrevistaDevolucao != null && dataFinal.isAfter(dataPrevistaDevolucao)) {
            return (int) ChronoUnit.DAYS.between(dataPrevistaDevolucao, dataFinal);
        }
        return 0;
    }

    public boolean estaAtrasado() {
        return diasAtraso() > 0;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public Long getItemAcervoId() { return itemAcervoId; }
    public void setItemAcervoId(Long itemAcervoId) { this.itemAcervoId = itemAcervoId; }
    public LocalDate getDataEmprestimo() { return dataEmprestimo; }
    public void setDataEmprestimo(LocalDate dataEmprestimo) { this.dataEmprestimo = dataEmprestimo; }
    public LocalDate getDataPrevistaDevolucao() { return dataPrevistaDevolucao; }
    public void setDataPrevistaDevolucao(LocalDate dataPrevistaDevolucao) { this.dataPrevistaDevolucao = dataPrevistaDevolucao; }
    public LocalDate getDataDevolucao() { return dataDevolucao; }
    public void setDataDevolucao(LocalDate dataDevolucao) { this.dataDevolucao = dataDevolucao; }
    public int getRenovacoes() { return renovacoes; }
    public void setRenovacoes(int renovacoes) { this.renovacoes = renovacoes; }
    public StatusEmprestimo getStatus() { return status; }
    public void setStatus(StatusEmprestimo status) { this.status = status; }
}
