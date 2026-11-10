package br.com.bibliotecasolidaria.facade;

import br.com.bibliotecasolidaria.dto.DoacaoDTO;
import br.com.bibliotecasolidaria.model.*;
import br.com.bibliotecasolidaria.observer.EventoBiblioteca;
import br.com.bibliotecasolidaria.observer.PublicadorEventos;
import br.com.bibliotecasolidaria.repository.ItemAcervoRepositorio;
import br.com.bibliotecasolidaria.repository.LivroRepositorio;
import br.com.bibliotecasolidaria.repository.UsuarioRepositorio;
import br.com.bibliotecasolidaria.service.BonificacaoService;
import br.com.bibliotecasolidaria.service.DoacaoService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public class DoacaoFacade {
    private final UsuarioRepositorio usuarioRepo;
    private final DoacaoService doacaoService;
    private final LivroRepositorio livroRepo;
    private final ItemAcervoRepositorio itemRepo;
    private final BonificacaoService bonificacaoService;
    private final PublicadorEventos publicador;

    public DoacaoFacade(UsuarioRepositorio ur, DoacaoService ds, LivroRepositorio lr,
                        ItemAcervoRepositorio ir, BonificacaoService bs, PublicadorEventos pub) {
        this.usuarioRepo = ur;
        this.doacaoService = ds;
        this.livroRepo = lr;
        this.itemRepo = ir;
        this.bonificacaoService = bs;
        this.publicador = pub;
    }

    public Doacao registrarDoacao(DoacaoDTO dto) {
        Usuario user = usuarioRepo.buscarPorId(dto.getIdDoador())
                .orElseThrow(() -> new IllegalArgumentException("Doador não encontrado."));
        if (!(user instanceof Doador doador)) {
            throw new IllegalStateException("Usuário não registrado como Doador.");
        }

        Doacao doacao;
        String tipo = dto.getTipo() != null ? dto.getTipo().toUpperCase() : "LIVRO_FISICO";
        Map<String, Object> d = dto.getDadosItem();

        if ("EBOOK".equals(tipo)) {
            doacao = new DoacaoEbook(null, doador.getId(), LocalDate.now(), StatusDoacao.REGISTRADA,
                    (String) d.get("titulo"), (String) d.get("autor"), (String) d.get("arquivoUrl"), (String) d.get("formato"));
        } else if ("DINHEIRO".equals(tipo)) {
            BigDecimal val = d.get("valor") instanceof BigDecimal bd ? bd : new BigDecimal(String.valueOf(d.get("valor")));
            doacao = new DoacaoDinheiro(null, doador.getId(), LocalDate.now(), StatusDoacao.REGISTRADA,
                    val, (String) d.get("meioPagamento"), (String) d.get("comprovante"));
        } else {
            int qtd = d.get("quantidade") instanceof Number n ? n.intValue() : 1;
            doacao = new DoacaoLivroFisico(null, doador.getId(), LocalDate.now(), StatusDoacao.REGISTRADA,
                    (String) d.get("titulo"), (String) d.get("autor"), (String) d.get("isbn"),
                    CategoriaLivro.OUTRO, EstadoConservacao.BOM, qtd);
        }

        if (doacao.avaliar()) {
            doacao.aceitar();
            if (doacao instanceof DoacaoLivroFisico df) {
                Livro l = livroRepo.salvar(new Livro(null, df.getTitulo(), df.getAutor(), "Solidária", df.getIsbn(), df.getCategoria(), 2024));
                itemRepo.salvar(new ExemplarFisico(null, l.getId(), StatusItem.DISPONIVEL, "TOMBO-" + l.getId(), df.getEstado(), "Estante Doações"));
            }
            bonificacaoService.processar(doador, doacao);
            publicador.notificar(new EventoBiblioteca(TipoEvento.DOACAO_ACEITA, doador, "Doação aceita com sucesso!", Map.of("doadorId", doador.getId())));
        } else {
            doacao.recusar("Não atendeu critérios mínimos");
        }

        return doacaoService.salvar(doacao);
    }
}
