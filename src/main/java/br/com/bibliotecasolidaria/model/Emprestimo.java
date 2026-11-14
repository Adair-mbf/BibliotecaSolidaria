package br.com.bibliotecasolidaria.model;

import br.com.bibliotecasolidaria.exception.NegocioException;
import br.com.bibliotecasolidaria.exception.ValidacaoException;
import br.com.bibliotecasolidaria.model.enums.StatusEmprestimo;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** Empréstimo de um item do acervo a um usuário do site. */
public class Emprestimo {
    private Long id;
    private Long usuarioId;
    private Long itemAcervoId;
    private LocalDate dataEmprestimo;
    private LocalDate dataPrevistaDevolucao;
    private LocalDate dataDevolucao;
    private int renovacoes;
    private StatusEmprestimo status;

    public Emprestimo(Long id, Long usuarioId, Long itemAcervoId, LocalDate dataEmprestimo, LocalDate dataPrevistaDevolucao, LocalDate dataDevolucao, int renovacoes, StatusEmprestimo status) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.itemAcervoId = itemAcervoId;
        this.dataEmprestimo = dataEmprestimo;
        this.dataPrevistaDevolucao = dataPrevistaDevolucao;
        this.dataDevolucao = dataDevolucao;
        this.renovacoes = renovacoes;
        this.status = status;
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

    public void renovar(LocalDate novoPrazo) {
        if (status == StatusEmprestimo.DEVOLVIDO) {
            throw new NegocioException("Empréstimo já devolvido não pode ser renovado.");
        }
        if (novoPrazo == null || !novoPrazo.isAfter(dataPrevistaDevolucao)) {
            throw new ValidacaoException("O novo prazo deve ser posterior ao prazo atual.");
        }
        dataPrevistaDevolucao = novoPrazo;
        renovacoes++;
    }

    public void registrarDevolucao() { registrarDevolucao(LocalDate.now()); }

    public void registrarDevolucao(LocalDate data) {
        if (status == StatusEmprestimo.DEVOLVIDO) {
            throw new NegocioException("O empréstimo já foi devolvido.");
        }
        if (data == null || data.isBefore(dataEmprestimo)) {
            throw new ValidacaoException("A data de devolução não pode ser anterior ao empréstimo.");
        }
        dataDevolucao = data;
        status = StatusEmprestimo.DEVOLVIDO;
    }

    public void marcarAtrasado() {
        if (status == StatusEmprestimo.ATIVO) {
            status = StatusEmprestimo.ATRASADO;
        }
    }

    public boolean estaEmAberto() { return status != StatusEmprestimo.DEVOLVIDO; }

    public int diasAtraso() { return diasAtraso(LocalDate.now()); }

    /** Dias de atraso na data de referência; se já devolvido, usa a data da devolução. */
    public int diasAtraso(LocalDate referencia) {
        LocalDate efetiva = dataDevolucao != null ? dataDevolucao : referencia;
        long dias = ChronoUnit.DAYS.between(dataPrevistaDevolucao, efetiva);
        return dias > 0 ? (int) dias : 0;
    }

    public boolean estaAtrasado() { return estaAtrasado(LocalDate.now()); }

    public boolean estaAtrasado(LocalDate referencia) {
        return estaEmAberto() && referencia.isAfter(dataPrevistaDevolucao);
    }

    @Override
    public String toString() {
        return "Emprestimo{" + "id=" + getId() + ", " + "usuarioId=" + getUsuarioId() + ", " + "itemAcervoId=" + getItemAcervoId() + ", " + "dataEmprestimo=" + getDataEmprestimo() + ", " + "dataPrevistaDevolucao=" + getDataPrevistaDevolucao() + ", " + "dataDevolucao=" + getDataDevolucao() + ", " + "renovacoes=" + getRenovacoes() + ", " + "status=" + getStatus() + "}";
    }
}
