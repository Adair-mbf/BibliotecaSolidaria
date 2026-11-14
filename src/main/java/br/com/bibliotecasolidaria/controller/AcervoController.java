package br.com.bibliotecasolidaria.controller;

import br.com.bibliotecasolidaria.exception.ValidacaoException;
import br.com.bibliotecasolidaria.model.Ebook;
import br.com.bibliotecasolidaria.model.ExemplarFisico;
import br.com.bibliotecasolidaria.model.ItemAcervo;
import br.com.bibliotecasolidaria.model.Livro;
import br.com.bibliotecasolidaria.model.enums.CategoriaLivro;
import br.com.bibliotecasolidaria.model.enums.EstadoConservacao;
import br.com.bibliotecasolidaria.model.enums.StatusItem;
import br.com.bibliotecasolidaria.model.enums.TipoItemAcervo;
import br.com.bibliotecasolidaria.service.AcervoService;

import java.util.List;

public class AcervoController extends BaseController {
    private final AcervoService acervo;

    public AcervoController(AcervoService acervo) {
        this.acervo = acervo;
    }

    /** Todos os campos são opcionais: titulo, categoria, tipo. */
    public Resposta consultarDisponiveis(Requisicao req) {
        return executar(() -> lista("item(ns) disponível(is)", acervo.consultarDisponiveis(req.textoOpcional("titulo"),
                req.enumeracaoOpcional("categoria", CategoriaLivro.class),
                req.enumeracaoOpcional("tipo", TipoItemAcervo.class))));
    }

    /** Consulta o acervo global pelo ISBN para pré-preencher o cadastro. */
    public Resposta preencherCadastro(Requisicao req) {
        return executar(() -> Resposta.ok("Dados encontrados no acervo global.", acervo.preencherCadastro(req.texto("isbn"))));
    }

    // ----------------------------------------------------------------- livros

    public Resposta cadastrarLivro(Requisicao req) {
        return executar(() -> {
            Livro livro = new Livro(null, req.texto("titulo"), req.texto("autor"), req.textoOpcional("editora"),
                    req.textoOpcional("isbn"), req.enumeracao("categoria", CategoriaLivro.class),
                    req.inteiro("anoPublicacao"));
            return Resposta.criado("Livro cadastrado.", acervo.cadastrarLivro(livro));
        });
    }

    public Resposta buscarLivro(Requisicao req) {
        return executar(() -> Resposta.ok("Livro encontrado.", acervo.buscarLivro(req.longo("id"))));
    }

    /** Filtros opcionais: titulo, categoria, isbn (sem filtros lista todos). */
    public Resposta listarLivros(Requisicao req) {
        return executar(() -> {
            List<Livro> livros;
            if (req.tem("isbn")) {
                livros = acervo.buscarPorIsbn(req.texto("isbn")).map(List::of).orElse(List.of());
            } else if (req.tem("titulo")) {
                livros = acervo.buscarPorTitulo(req.texto("titulo"));
            } else if (req.tem("categoria")) {
                livros = acervo.buscarPorCategoria(req.enumeracao("categoria", CategoriaLivro.class));
            } else {
                livros = acervo.listarLivros();
            }
            return lista("livro(s)", livros);
        });
    }

    /** Campos: id + qualquer de titulo, autor, editora, isbn, categoria, anoPublicacao. */
    public Resposta atualizarLivro(Requisicao req) {
        return executar(() -> {
            Livro l = acervo.buscarLivro(req.longo("id"));
            if (req.tem("titulo")) l.setTitulo(req.texto("titulo"));
            if (req.tem("autor")) l.setAutor(req.texto("autor"));
            if (req.tem("editora")) l.setEditora(req.texto("editora"));
            if (req.tem("isbn")) l.setIsbn(req.texto("isbn"));
            if (req.tem("categoria")) l.setCategoria(req.enumeracao("categoria", CategoriaLivro.class));
            if (req.tem("anoPublicacao")) l.setAnoPublicacao(req.inteiro("anoPublicacao"));
            return Resposta.ok("Livro atualizado.", acervo.atualizarLivro(l));
        });
    }

    public Resposta removerLivro(Requisicao req) {
        return executar(() -> {
            acervo.removerLivro(req.longo("id"));
            return Resposta.ok("Livro removido.", null);
        });
    }

    // ------------------------------------------------------------------ itens

    /** Campos: livroId, tipo (EXEMPLAR_FISICO: codigoTombo, estado, localizacao; EBOOK: arquivoUrl, formato). */
    public Resposta cadastrarItem(Requisicao req) {
        return executar(() -> {
            long livroId = req.longo("livroId");
            ItemAcervo item = switch (req.enumeracao("tipo", TipoItemAcervo.class)) {
                case EXEMPLAR_FISICO -> new ExemplarFisico(null, livroId, StatusItem.DISPONIVEL,
                        req.texto("codigoTombo"), req.enumeracao("estado", EstadoConservacao.class),
                        req.texto("localizacao"));
                case EBOOK -> new Ebook(null, livroId, StatusItem.DISPONIVEL, req.texto("arquivoUrl"),
                        req.texto("formato"));
            };
            return Resposta.criado("Item cadastrado.", acervo.cadastrarItem(item));
        });
    }

    public Resposta buscarItem(Requisicao req) {
        return executar(() -> Resposta.ok("Item encontrado.", acervo.buscarItem(req.longo("id"))));
    }

    /** Filtro opcional: livroId. */
    public Resposta listarItens(Requisicao req) {
        return executar(() -> {
            Long livroId = req.longoOpcional("livroId");
            return lista("item(ns)", livroId == null ? acervo.listarItens() : acervo.listarItensPorLivro(livroId));
        });
    }

    /** Campos: id + status, livroId e os campos do tipo (codigoTombo/estado/localizacao ou arquivoUrl/formato). */
    public Resposta atualizarItem(Requisicao req) {
        return executar(() -> {
            ItemAcervo item = acervo.buscarItem(req.longo("id"));
            if (req.tem("livroId")) item.setLivroId(req.longo("livroId"));
            if (req.tem("status")) item.setStatus(req.enumeracao("status", StatusItem.class));
            if (item instanceof ExemplarFisico e) {
                if (req.tem("codigoTombo")) e.setCodigoTombo(req.texto("codigoTombo"));
                if (req.tem("estado")) e.setEstado(req.enumeracao("estado", EstadoConservacao.class));
                if (req.tem("localizacao")) e.setLocalizacao(req.texto("localizacao"));
            } else if (item instanceof Ebook b) {
                if (req.tem("arquivoUrl")) b.setArquivoUrl(req.texto("arquivoUrl"));
                if (req.tem("formato")) b.setFormato(req.texto("formato"));
            } else {
                throw new ValidacaoException("Tipo de item desconhecido.");
            }
            return Resposta.ok("Item atualizado.", acervo.atualizarItem(item));
        });
    }

    public Resposta removerItem(Requisicao req) {
        return executar(() -> {
            acervo.removerItem(req.longo("id"));
            return Resposta.ok("Item removido.", null);
        });
    }
}
