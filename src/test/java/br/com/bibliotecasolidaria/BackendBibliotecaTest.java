package br.com.bibliotecasolidaria;

import br.com.bibliotecasolidaria.adapter.PagamentoAdapter;
import br.com.bibliotecasolidaria.controller.EmprestimoController;
import br.com.bibliotecasolidaria.dto.DadosUsuario;
import br.com.bibliotecasolidaria.dto.Devolucao;
import br.com.bibliotecasolidaria.facade.EmprestimoFacade;
import br.com.bibliotecasolidaria.factory.FabricaRegistry;
import br.com.bibliotecasolidaria.model.*;
import br.com.bibliotecasolidaria.observer.*;
import br.com.bibliotecasolidaria.repository.*;
import br.com.bibliotecasolidaria.service.*;
import br.com.bibliotecasolidaria.strategy.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class BackendBibliotecaTest {

    private EmprestimoFacade emprestimoFacade;
    private EmprestimoController emprestimoController;
    private UsuarioRepositorio usuarioRepo;
    private ItemAcervoRepositorio itemRepo;
    private EmprestimoRepositorio emprestimoRepo;
    private MultaRepositorio multaRepo;
    private PublicadorEventos publicador;

    @BeforeEach
    void setup() {
        usuarioRepo = new UsuarioRepositorio();
        itemRepo = new ItemAcervoRepositorio();
        emprestimoRepo = new EmprestimoRepositorio();
        multaRepo = new MultaRepositorio();
        publicador = new PublicadorEventos();

        PoliticaRenovacao politica = new LimiteFixoRenovacao(2, 7);
        CalculoMultaStrategy calculoMulta = new MultaFixaDiaria(new BigDecimal("2.50"));

        CanalEmail email = new CanalEmail();
        publicador.registrar(new NotificadorDisponibilidade(List.of(email)), TipoEvento.LIVRO_DISPONIVEL);

        EmprestimoService empService = new EmprestimoService(emprestimoRepo, politica);
        MultaService multaService = new MultaService(multaRepo, usuarioRepo, calculoMulta, new PagamentoAdapter());

        emprestimoFacade = new EmprestimoFacade(empService, multaService, usuarioRepo, itemRepo, publicador);
        emprestimoController = new EmprestimoController(emprestimoFacade);
    }

    @Test
    @DisplayName("Deve cadastrar usuário via Factory Method com sucesso")
    void testCriacaoUsuarioFactory() {
        FabricaRegistry registry = new FabricaRegistry();
        DadosUsuario dados = new DadosUsuario("João Silva", "joao@email.com", "senha123", 
                TipoUsuario.USUARIO_SITE, Map.of("telefone", "6499999999", "endereco", "Centro"));

        Usuario user = registry.obter(TipoUsuario.USUARIO_SITE).cadastrar(dados);
        assertNotNull(user);
        assertEquals(TipoUsuario.USUARIO_SITE, user.getTipo());
        assertTrue(user.autenticar("senha123"));
    }

    @Test
    @DisplayName("Fluxo completo de empréstimo e devolução com cálculo de multa")
    void testEmprestarEDevolverComAtraso() {
        UsuarioSite user = new UsuarioSite(1L, "Maria", "maria@mail.com", "hash", true, "123", "Rua 1");
        usuarioRepo.salvar(user);

        ExemplarFisico livro = new ExemplarFisico(10L, 100L, StatusItem.DISPONIVEL, "TOMBO-01", EstadoConservacao.OTIMO, "E-1");
        itemRepo.salvar(livro);

        var respEmprestimo = emprestimoController.emprestar(1L, 10L);
        assertEquals(201, respEmprestimo.status());
        assertFalse(livro.estaDisponivel());

        Emprestimo emp = respEmprestimo.dados();
        emp.setDataPrevistaDevolucao(LocalDate.now().minusDays(4));
        emprestimoRepo.salvar(emp);

        var respDevolucao = emprestimoController.devolver(emp.getId());
        assertEquals(200, respDevolucao.status());

        Devolucao dev = respDevolucao.dados();
        assertEquals(4, dev.getAtrasoEmDias());
        assertNotNull(dev.getMulta());
        assertEquals(new BigDecimal("10.00"), dev.getMulta().getValor());
        assertTrue(livro.estaDisponivel());
    }

    @Test
    @DisplayName("Deve barrar novo empréstimo quando usuário tiver pendências")
    void testBloqueioEmprestimoComMultaPendente() {
        UsuarioSite user = new UsuarioSite(2L, "Carlos", "carlos@mail.com", "hash", true, "456", "Rua 2");
        usuarioRepo.salvar(user);

        multaRepo.salvar(new Multa(null, 99L, 2L, new BigDecimal("15.00"), LocalDate.now(), null, StatusMulta.PENDENTE));

        ExemplarFisico item = new ExemplarFisico(11L, 101L, StatusItem.DISPONIVEL, "TOMBO-02", EstadoConservacao.BOM, "E-2");
        itemRepo.salvar(item);

        var resp = emprestimoController.emprestar(2L, 11L);
        assertEquals(400, resp.status());
        assertTrue(resp.mensagem().contains("multas pendentes"));
    }
}
