package br.com.bibliotecasolidaria.model;

public abstract class ItemAcervo {
    protected Long id;
    protected Long livroId;
    protected StatusItem status = StatusItem.DISPONIVEL;

    public ItemAcervo() {}

    public ItemAcervo(Long id, Long livroId, StatusItem status) {
        this.id = id;
        this.livroId = livroId;
        this.status = status != null ? status : StatusItem.DISPONIVEL;
    }

    public boolean estaDisponivel() {
        return this.status == StatusItem.DISPONIVEL;
    }

    public void marcarEmprestado() {
        this.status = StatusItem.EMPRESTADO;
    }

    public void marcarDisponivel() {
        this.status = StatusItem.DISPONIVEL;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getLivroId() { return livroId; }
    public void setLivroId(Long livroId) { this.livroId = livroId; }
    public StatusItem getStatus() { return status; }
    public void setStatus(StatusItem status) { this.status = status; }
}
