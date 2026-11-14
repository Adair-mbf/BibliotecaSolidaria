package br.com.bibliotecasolidaria.config;

import br.com.bibliotecasolidaria.adapter.AcervoGlobalAdapter;
import br.com.bibliotecasolidaria.adapter.AcervoGlobalProvider;
import br.com.bibliotecasolidaria.adapter.ApiAcervoGlobal;
import br.com.bibliotecasolidaria.adapter.ApiPagamentoExterna;
import br.com.bibliotecasolidaria.adapter.GatewayPagamento;
import br.com.bibliotecasolidaria.adapter.PagamentoAdapter;
import br.com.bibliotecasolidaria.controller.AcervoController;
import br.com.bibliotecasolidaria.controller.BonificacaoController;
import br.com.bibliotecasolidaria.controller.DoacaoController;
import br.com.bibliotecasolidaria.controller.EmprestimoController;
import br.com.bibliotecasolidaria.controller.MultaController;
import br.com.bibliotecasolidaria.controller.NotificacaoController;
import br.com.bibliotecasolidaria.controller.RelatorioController;
import br.com.bibliotecasolidaria.controller.ReservaController;
import br.com.bibliotecasolidaria.controller.UsuarioController;
import br.com.bibliotecasolidaria.facade.DoacaoFacade;
import br.com.bibliotecasolidaria.facade.EmprestimoFacade;
import br.com.bibliotecasolidaria.factory.DoadorFactory;
import br.com.bibliotecasolidaria.factory.FabricaRegistry;
import br.com.bibliotecasolidaria.factory.FuncionarioFactory;
import br.com.bibliotecasolidaria.factory.UsuarioSiteFactory;
import br.com.bibliotecasolidaria.model.enums.TipoEvento;
import br.com.bibliotecasolidaria.model.enums.TipoUsuario;
import br.com.bibliotecasolidaria.observer.CanalEmail;
import br.com.bibliotecasolidaria.observer.CanalNotificacao;
import br.com.bibliotecasolidaria.observer.CanalPainelInterno;
import br.com.bibliotecasolidaria.observer.NotificadorDisponibilidade;
import br.com.bibliotecasolidaria.observer.NotificadorDoacaoAceita;
import br.com.bibliotecasolidaria.observer.NotificadorVencimento;
import br.com.bibliotecasolidaria.observer.PublicadorEventos;
import br.com.bibliotecasolidaria.persistence.BonificacaoRepositorio;
import br.com.bibliotecasolidaria.persistence.ConexaoBanco;
import br.com.bibliotecasolidaria.persistence.DoacaoRepositorio;
import br.com.bibliotecasolidaria.persistence.EmprestimoRepositorio;
import br.com.bibliotecasolidaria.persistence.ItemAcervoRepositorio;
import br.com.bibliotecasolidaria.persistence.LivroRepositorio;
import br.com.bibliotecasolidaria.persistence.MultaRepositorio;
import br.com.bibliotecasolidaria.persistence.NotificacaoRepositorio;
import br.com.bibliotecasolidaria.persistence.ReservaRepositorio;
import br.com.bibliotecasolidaria.persistence.UsuarioRepositorio;
import br.com.bibliotecasolidaria.service.AcervoService;
import br.com.bibliotecasolidaria.service.AgendadorVencimentos;
import br.com.bibliotecasolidaria.service.BonificacaoService;
import br.com.bibliotecasolidaria.service.DoacaoService;
import br.com.bibliotecasolidaria.service.EmprestimoService;
import br.com.bibliotecasolidaria.service.MultaService;
import br.com.bibliotecasolidaria.service.NotificacaoService;
import br.com.bibliotecasolidaria.service.RelatorioService;
import br.com.bibliotecasolidaria.service.ReservaService;
import br.com.bibliotecasolidaria.service.UsuarioService;
import br.com.bibliotecasolidaria.strategy.BonificacaoEmpresarial;
import br.com.bibliotecasolidaria.strategy.BonificacaoPorQuantidade;
import br.com.bibliotecasolidaria.strategy.BonificacaoPorValor;
import br.com.bibliotecasolidaria.strategy.CalculoMultaStrategy;
import br.com.bibliotecasolidaria.strategy.FaixaMulta;
import br.com.bibliotecasolidaria.strategy.LimiteFixoRenovacao;
import br.com.bibliotecasolidaria.strategy.MultaFixaDiaria;
import br.com.bibliotecasolidaria.strategy.MultaProgressiva;
import br.com.bibliotecasolidaria.strategy.PoliticaRenovacao;
import br.com.bibliotecasolidaria.strategy.SemRenovacao;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.List;
import java.util.function.Consumer;

/**
 * Raiz de composição: cria e conecta repositórios, serviços, facades e controllers.
 * Os campos são públicos e finais por se tratar apenas de um contêiner de dependências.
 */
public final class ContextoAplicacao {
    public final ConexaoBanco conexao;
    public final ConfiguracaoSistema config;
    public final Clock clock;

    public final UsuarioRepositorio usuarioRepositorio;
    public final DoacaoRepositorio doacaoRepositorio;
    public final LivroRepositorio livroRepositorio;
    public final ItemAcervoRepositorio itemAcervoRepositorio;
    public final EmprestimoRepositorio emprestimoRepositorio;
    public final MultaRepositorio multaRepositorio;
    public final ReservaRepositorio reservaRepositorio;
    public final BonificacaoRepositorio bonificacaoRepositorio;
    public final NotificacaoRepositorio notificacaoRepositorio;

    public final PublicadorEventos publicador;
    public final GatewayPagamento gatewayPagamento;
    public final AcervoGlobalProvider acervoGlobal;

    public final UsuarioService usuarioService;
    public final AcervoService acervoService;
    public final ReservaService reservaService;
    public final EmprestimoService emprestimoService;
    public final MultaService multaService;
    public final DoacaoService doacaoService;
    public final BonificacaoService bonificacaoService;
    public final NotificacaoService notificacaoService;
    public final RelatorioService relatorioService;
    public final AgendadorVencimentos agendadorVencimentos;

    public final EmprestimoFacade emprestimoFacade;
    public final DoacaoFacade doacaoFacade;

    public final UsuarioController usuarioController;
    public final AcervoController acervoController;
    public final EmprestimoController emprestimoController;
    public final MultaController multaController;
    public final ReservaController reservaController;
    public final DoacaoController doacaoController;
    public final BonificacaoController bonificacaoController;
    public final NotificacaoController notificacaoController;
    public final RelatorioController relatorioController;

    private ContextoAplicacao(ConexaoBanco conexao, ConfiguracaoSistema config, Clock clock,
                              Consumer<String> transporteEmail) {
        this.conexao = conexao;
        this.config = config;
        this.clock = clock;

        usuarioRepositorio = new UsuarioRepositorio(conexao);
        doacaoRepositorio = new DoacaoRepositorio(conexao);
        livroRepositorio = new LivroRepositorio(conexao);
        itemAcervoRepositorio = new ItemAcervoRepositorio(conexao);
        emprestimoRepositorio = new EmprestimoRepositorio(conexao);
        multaRepositorio = new MultaRepositorio(conexao);
        reservaRepositorio = new ReservaRepositorio(conexao);
        bonificacaoRepositorio = new BonificacaoRepositorio(conexao);
        notificacaoRepositorio = new NotificacaoRepositorio(conexao);

        gatewayPagamento = new PagamentoAdapter(new ApiPagamentoExterna(), clock);
        acervoGlobal = new AcervoGlobalAdapter(new ApiAcervoGlobal());

        publicador = new PublicadorEventos();
        List<CanalNotificacao> canais = List.of(new CanalPainelInterno(notificacaoRepositorio),
                new CanalEmail(usuarioRepositorio, transporteEmail));
        publicador.registrar(TipoEvento.LIVRO_DISPONIVEL, new NotificadorDisponibilidade(canais));
        publicador.registrar(TipoEvento.VENCIMENTO_PROXIMO, new NotificadorVencimento(canais));
        publicador.registrar(TipoEvento.DOACAO_ACEITA, new NotificadorDoacaoAceita(canais));

        FabricaRegistry fabricas = new FabricaRegistry();
        fabricas.registrar(TipoUsuario.DOADOR, new DoadorFactory(usuarioRepositorio));
        fabricas.registrar(TipoUsuario.USUARIO_SITE, new UsuarioSiteFactory(usuarioRepositorio));
        fabricas.registrar(TipoUsuario.FUNCIONARIO, new FuncionarioFactory(usuarioRepositorio));

        usuarioService = new UsuarioService(fabricas, usuarioRepositorio);
        acervoService = new AcervoService(livroRepositorio, itemAcervoRepositorio, acervoGlobal, clock);
        reservaService = new ReservaService(reservaRepositorio, usuarioService, acervoService, publicador, clock);
        PoliticaRenovacao politica = config.getMaxRenovacoes() == 0 ? new SemRenovacao()
                : new LimiteFixoRenovacao(config.getMaxRenovacoes(), config.getDiasEmprestimo(), clock);
        emprestimoService = new EmprestimoService(emprestimoRepositorio, acervoService, reservaService, config,
                politica, clock);
        multaService = new MultaService(multaRepositorio, emprestimoService, usuarioService, gatewayPagamento,
                estrategiaDeMulta(config), clock);
        doacaoService = new DoacaoService(doacaoRepositorio, usuarioService, publicador, clock);
        bonificacaoService = new BonificacaoService(bonificacaoRepositorio, usuarioService, List.of(
                new BonificacaoPorQuantidade(10), new BonificacaoPorValor(new BigDecimal("100.00")),
                new BonificacaoEmpresarial(5)), clock);
        notificacaoService = new NotificacaoService(notificacaoRepositorio, usuarioService, clock);
        relatorioService = new RelatorioService(emprestimoRepositorio, multaRepositorio, doacaoRepositorio, clock);
        agendadorVencimentos = new AgendadorVencimentos(emprestimoService, acervoService, usuarioService,
                publicador, notificacaoRepositorio, config, clock);

        emprestimoFacade = new EmprestimoFacade(usuarioService, acervoService, emprestimoService, multaService,
                reservaService, conexao);
        doacaoFacade = new DoacaoFacade(doacaoService, usuarioService, acervoService, bonificacaoService,
                reservaService, conexao);

        usuarioController = new UsuarioController(usuarioService);
        acervoController = new AcervoController(acervoService);
        emprestimoController = new EmprestimoController(emprestimoFacade, emprestimoService);
        multaController = new MultaController(multaService);
        reservaController = new ReservaController(reservaService, conexao);
        doacaoController = new DoacaoController(doacaoFacade, doacaoService);
        bonificacaoController = new BonificacaoController(bonificacaoService);
        notificacaoController = new NotificacaoController(notificacaoService, agendadorVencimentos, conexao);
        relatorioController = new RelatorioController(relatorioService);
    }

    public static ContextoAplicacao criar(ConexaoBanco conexao, ConfiguracaoSistema config, Clock clock,
                                          Consumer<String> transporteEmail) {
        return new ContextoAplicacao(conexao, config, clock, transporteEmail);
    }

    /** Contexto de produção: e-mails simulados vão para o log. */
    public static ContextoAplicacao criar(ConexaoBanco conexao, ConfiguracaoSistema config) {
        java.util.logging.Logger log = java.util.logging.Logger.getLogger(CanalEmail.class.getName());
        return criar(conexao, config, Clock.systemDefaultZone(), log::info);
    }

    private static CalculoMultaStrategy estrategiaDeMulta(ConfiguracaoSistema config) {
        BigDecimal v = config.getValorMultaDia();
        if ("progressiva".equalsIgnoreCase(config.getEstrategiaMulta())) {
            return new MultaProgressiva(List.of(
                    new FaixaMulta(1, 7, v),
                    new FaixaMulta(8, 14, v.multiply(BigDecimal.valueOf(2))),
                    new FaixaMulta(15, 30, v.multiply(BigDecimal.valueOf(3)))),
                    v.multiply(BigDecimal.valueOf(60)));
        }
        return new MultaFixaDiaria(v);
    }
}
