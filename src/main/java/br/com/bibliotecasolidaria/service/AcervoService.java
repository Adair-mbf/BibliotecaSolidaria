package br.com.bibliotecasolidaria.service;

import br.com.bibliotecasolidaria.adapter.AcervoGlobalProvider;
import br.com.bibliotecasolidaria.dto.DadosLivro;
import br.com.bibliotecasolidaria.model.ItemAcervo;
import br.com.bibliotecasolidaria.model.Livro;
import br.com.bibliotecasolidaria.repository.ItemAcervoRepositorio;
import br.com.bibliotecasolidaria.repository.LivroRepositorio;
import java.util.List;

public class AcervoService {
    private final LivroRepositorio livroRepo;
    private final ItemAcervoRepositorio itemRepo;
    private final AcervoGlobalProvider globalProvider;

    public AcervoService(LivroRepositorio lr, ItemAcervoRepositorio ir, AcervoGlobalProvider gp) {
        this.livroRepo = lr;
        this.itemRepo = ir;
        this.globalProvider = gp;
    }

    public Livro cadastrarLivro(Livro livro) {
        return livroRepo.salvar(livro);
    }

    public ItemAcervo cadastrarItem(ItemAcervo item) {
        return itemRepo.salvar(item);
    }

    public List<ItemAcervo> listarDisponiveis() {
        return itemRepo.listarDisponiveis();
    }

    public DadosLivro preencherCadastro(String isbn) {
        return globalProvider.buscarPorIsbn(isbn).orElse(null);
    }
}
