package br.com.bibliotecasolidaria.model;

import br.com.bibliotecasolidaria.exception.NegocioException;
import br.com.bibliotecasolidaria.model.enums.StatusItem;
import br.com.bibliotecasolidaria.model.enums.TipoItemAcervo;

/** Item do acervo (classe base de ExemplarFisico e Ebook). */
public abstract class ItemAcervo {
    private Long id;
    private Long livroId;
    private StatusItem status;

    protected ItemAcervo(Long id, Long livroId, StatusItem status) {
        this.id = id;
        this.livroId = livroId;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getLivroId() { return livroId; }
    public void setLivroId(Long livroId) { this.livroId = livroId; }
    public StatusItem getStatus() { return status; }
    public void setStatus(StatusItem status) { this.status = status; }

    public abstract TipoItemAcervo getTipo();

    public boolean estaDisponivel() { return status == StatusItem.DISPONIVEL; }

    public void marcarEmprestado() {
        if (status != StatusItem.DISPONIVEL && status != StatusItem.RESERVADO) {
            throw new NegocioException("O item não está disponível para empréstimo (status " + status + ").");
        }
        status = StatusItem.EMPRESTADO;
    }

    public void marcarDisponivel() { status = StatusItem.DISPONIVEL; }

    public void marcarReservado() { status = StatusItem.RESERVADO; }

    @Override
    public String toString() {
        return "ItemAcervo{" + "id=" + getId() + ", " + "livroId=" + getLivroId() + ", " + "status=" + getStatus() + "}";
    }
}
