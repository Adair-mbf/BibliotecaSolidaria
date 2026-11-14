package br.com.bibliotecasolidaria.service;

import br.com.bibliotecasolidaria.adapter.AcervoGlobalProvider;
import br.com.bibliotecasolidaria.dto.DadosLivro;
import br.com.bibliotecasolidaria.dto.ItemConsultado;
import br.com.bibliotecasolidaria.exception.ConflitoException;
import br.com.bibliotecasolidaria.exception.NaoEncontradoException;
import br.com.bibliotecasolidaria.exception.NegocioException;
import br.com.bibliotecasolidaria.exception.ValidacaoException;
import br.com.bibliotecasolidaria.model.Doacao;
import br.com.bibliotecasolidaria.model.DoacaoEbook;
import br.com.bibliotecasolidaria.model.DoacaoLivroFisico;
import br.com.bibliotecasolidaria.model.Ebook;
import br.com.bibliotecasolidaria.model.ExemplarFisico;
import br.com.bibliotecasolidaria.model.ItemAcervo;
import br.com.bibliotecasolidaria.model.Livro;
import br.com.bibliotecasolidaria.model.enums.CategoriaLivro;
import br.com.bibliotecasolidaria.model.enums.StatusItem;
import br.com.bibliotecasolidaria.model.enums.TipoItemAcervo;
import br.com.bibliotecasolidaria.persistence.ItemAcervoRepositorio;
import br.com.bibliotecasolidaria.persistence.LivroRepositorio;
import br.com.bibliotecasolidaria.util.Textos;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AcervoService {
    private final LivroRepositorio livros;
    private final ItemAcervoRepositorio itens;
    private final AcervoGlobalProvider acervoGlobal;
    private final Clock clock;

    public AcervoService(LivroRepositorio livros, ItemAcervoRepositorio itens, AcervoGlobalProvider acervoGlobal,
                         Clock clock) {
        this.livros = livros;
        this.itens = itens;
        this.acervoGlobal = acervoGlobal;
        this.clock = clock;
    }

    // ---------------------------------------------------------------- livros

    public Livro cadastrarLivro(Livro livro) {
        validarLivro(livro);
        if (!Textos.emBranco(livro.getIsbn()) && livros.buscarPorIsbn(livro.getIsbn()).isPresent()) {
            throw new ConflitoException("Já existe um livro com o ISBN " + livro.getIsbn() + ".");
        }
        livro.setId(null);
        return livros.salvar(livro);
    }

    public Livro atualizarLivro(Livro livro) {
        if (livro.getId() == null) {
            throw new ValidacaoException("Informe o id do livro.");
        }
        buscarLivro(livro.getId());
        validarLivro(livro);
        Optional<Livro> mesmoIsbn = Textos.emBranco(livro.getIsbn()) ? Optional.empty()
                : livros.buscarPorIsbn(livro.getIsbn());
        if (mesmoIsbn.isPresent() && !mesmoIsbn.get().getId().equals(livro.getId())) {
            throw new ConflitoException("Já existe outro livro com o ISBN " + livro.getIsbn() + ".");
        }
        return livros.salvar(livro);
    }

    public void removerLivro(long id) {
        buscarLivro(id);
        livros.remover(id); // falha com conflito se houver itens, reservas etc. vinculados
    }

    public Livro buscarLivro(long id) {
        return livros.buscarPorId(id).orElseThrow(() -> new NaoEncontradoException("Livro " + id + " não encontrado."));
    }

    public List<Livro> listarLivros() { return livros.listarTodos(); }

    public List<Livro> buscarPorTitulo(String titulo) { return livros.buscarPorTitulo(titulo); }

    public List<Livro> buscarPorCategoria(CategoriaLivro categoria) { return livros.buscarPorCategoria(categoria); }

    public Optional<Livro> buscarPorIsbn(String isbn) { return livros.buscarPorIsbn(isbn); }

    /** Consulta o acervo global (Adapter) para pré-preencher o cadastro de um livro. */
    public DadosLivro preencherCadastro(String isbn) {
        return acervoGlobal.buscarPorIsbn(isbn)
                .orElseThrow(() -> new NaoEncontradoException("ISBN " + isbn + " não encontrado no acervo global."));
    }

    private void validarLivro(Livro l) {
        if (Textos.emBranco(l.getTitulo())) throw new ValidacaoException("O título é obrigatório.");
        if (Textos.emBranco(l.getAutor())) throw new ValidacaoException("O autor é obrigatório.");
        if (l.getCategoria() == null) throw new ValidacaoException("A categoria é obrigatória.");
        if (l.getAnoPublicacao() < 0 || l.getAnoPublicacao() > LocalDate.now(clock).getYear() + 1) {
            throw new ValidacaoException("Ano de publicação inválido.");
        }
    }

    // ----------------------------------------------------------------- itens

    public ItemAcervo cadastrarItem(ItemAcervo item) {
        validarItem(item);
        item.setId(null);
        return itens.salvar(item);
    }

    public ItemAcervo atualizarItem(ItemAcervo item) {
        if (item.getId() == null) throw new ValidacaoException("Informe o id do item.");
        ItemAcervo atual = buscarItem(item.getId());
        if (atual.getTipo() != item.getTipo()) throw new ValidacaoException("O tipo de um item não pode mudar.");
        validarItem(item);
        return itens.salvar(item);
    }

    public void removerItem(long id) {
        ItemAcervo item = buscarItem(id);
        if (item.getStatus() == StatusItem.EMPRESTADO || item.getStatus() == StatusItem.RESERVADO) {
            throw new NegocioException("Não é possível remover um item " + item.getStatus() + ".");
        }
        itens.remover(id);
    }

    public ItemAcervo buscarItem(long id) {
        return itens.buscarPorId(id).orElseThrow(() -> new NaoEncontradoException("Item " + id + " não encontrado."));
    }

    public List<ItemAcervo> listarItens() { return itens.listarTodos(); }

    public List<ItemAcervo> listarItensPorLivro(long livroId) { return itens.listarPorLivro(livroId); }

    public List<ItemAcervo> listarDisponiveis() { return itens.listarDisponiveis(); }

    /** Itens disponíveis, filtrados (todos opcionais) por trecho do título, categoria e tipo de item. */
    public List<ItemConsultado> consultarDisponiveis(String titulo, CategoriaLivro categoria, TipoItemAcervo tipo) {
        Map<Long, Livro> cache = new HashMap<>();
        List<ItemConsultado> resultado = new ArrayList<>();
        for (ItemAcervo item : itens.listarDisponiveis()) {
            if (tipo != null && item.getTipo() != tipo) continue;
            Livro livro = cache.computeIfAbsent(item.getLivroId(), id -> livros.buscarPorId(id).orElse(null));
            if (livro == null) continue;
            if (categoria != null && livro.getCategoria() != categoria) continue;
            if (!Textos.emBranco(titulo) && !livro.getTitulo().toLowerCase().contains(titulo.trim().toLowerCase())) {
                continue;
            }
            resultado.add(new ItemConsultado(livro, item));
        }
        return resultado;
    }

    public boolean temItemDisponivel(long livroId) {
        return itens.listarPorLivro(livroId).stream().anyMatch(ItemAcervo::estaDisponivel);
    }

    public ItemAcervo marcarEmprestado(long itemId) {
        ItemAcervo item = buscarItem(itemId);
        item.marcarEmprestado();
        return itens.salvar(item);
    }

    public ItemAcervo marcarDisponivel(long itemId) {
        ItemAcervo item = buscarItem(itemId);
        item.marcarDisponivel();
        return itens.salvar(item);
    }

    public ItemAcervo marcarReservado(long itemId) {
        ItemAcervo item = buscarItem(itemId);
        item.marcarReservado();
        return itens.salvar(item);
    }

    private void validarItem(ItemAcervo item) {
        if (item.getLivroId() == null) throw new ValidacaoException("Informe o livro do item.");
        buscarLivro(item.getLivroId());
        if (item.getStatus() == null) item.setStatus(StatusItem.DISPONIVEL);
        if (item instanceof ExemplarFisico e) {
            if (Textos.emBranco(e.getCodigoTombo())) throw new ValidacaoException("O código de tombo é obrigatório.");
            if (e.getEstado() == null) throw new ValidacaoException("O estado de conservação é obrigatório.");
            if (Textos.emBranco(e.getLocalizacao())) throw new ValidacaoException("A localização é obrigatória.");
            Optional<ItemAcervo> mesmo = itens.buscarPorCodigoTombo(e.getCodigoTombo());
            if (mesmo.isPresent() && !mesmo.get().getId().equals(e.getId())) {
                throw new ConflitoException("O código de tombo " + e.getCodigoTombo() + " já está em uso.");
            }
        } else if (item instanceof Ebook b) {
            if (Textos.emBranco(b.getArquivoUrl())) throw new ValidacaoException("A URL do arquivo é obrigatória.");
            if (Textos.emBranco(b.getFormato())) throw new ValidacaoException("O formato do arquivo é obrigatório.");
        }
    }

    // --------------------------------------------------------------- doações

    /**
     * Inclui no acervo o que foi doado: um exemplar por unidade de livro físico (reaproveitando o livro de mesmo
     * ISBN ou criando-o) ou um e-book. Doações em dinheiro não geram itens.
     */
    public List<ItemAcervo> incluirDoacao(Doacao doacao) {
        List<ItemAcervo> criados = new ArrayList<>();
        if (doacao instanceof DoacaoLivroFisico d) {
            Livro livro = livros.buscarPorIsbn(d.getIsbn()).orElseGet(() -> livros.salvar(novoLivroDe(d)));
            for (int i = 0; i < d.getQuantidade(); i++) {
                criados.add(itens.salvar(new ExemplarFisico(null, livro.getId(), StatusItem.DISPONIVEL,
                        proximoTombo(livro.getId()), d.getEstado(), "Doações")));
            }
        } else if (doacao instanceof DoacaoEbook d) {
            Livro livro = livros.buscarPorTitulo(d.getTitulo()).stream()
                    .filter(l -> l.getAutor().equalsIgnoreCase(d.getAutor())
                            && l.getTitulo().equalsIgnoreCase(d.getTitulo()))
                    .findFirst().orElseGet(() -> livros.salvar(new Livro(null, d.getTitulo(), d.getAutor(), null,
                            null, CategoriaLivro.OUTRO, LocalDate.now(clock).getYear())));
            criados.add(itens.salvar(new Ebook(null, livro.getId(), StatusItem.DISPONIVEL, d.getArquivoUrl(),
                    d.getFormato().trim().toUpperCase())));
        }
        return criados;
    }

    private Livro novoLivroDe(DoacaoLivroFisico d) {
        Optional<DadosLivro> global = acervoGlobal.buscarPorIsbn(d.getIsbn());
        String editora = global.map(DadosLivro::editora).orElse(null);
        int ano = global.map(DadosLivro::anoPublicacao).filter(a -> a > 0).orElse(LocalDate.now(clock).getYear());
        return new Livro(null, d.getTitulo(), d.getAutor(), editora, d.getIsbn(), d.getCategoria(), ano);
    }

    private String proximoTombo(long livroId) {
        int n = itens.listarPorLivro(livroId).size() + 1;
        String codigo;
        do {
            codigo = String.format("LIV%05d-%03d", livroId, n++);
        } while (itens.buscarPorCodigoTombo(codigo).isPresent());
        return codigo;
    }
}
