package br.com.bibliotecasolidaria.controller;

import br.com.bibliotecasolidaria.support.BancoTestBase;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Verifica o contrato das respostas (códigos e mensagens) e um ciclo de CRUD completo pelos controllers. */
class ControllersTest extends BancoTestBase {
    private Resposta cadastrarUsuarioSite(String email) {
        return ctx.usuarioController.cadastrar(Requisicao.de("nome", "Ana", "email", email, "senha", "senha123",
                "tipo", "usuario_site", "telefone", "62999", "endereco", "Rua 1"));
    }

    private long idDe(Resposta r) {
        Object d = r.dados();
        try {
            return (Long) d.getClass().getMethod("getId").invoke(d);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    @Test
    void codigosDeRespostaParaCadaTipoDeErro() {
        assertEquals(201, cadastrarUsuarioSite("ana@x.com").codigo());
        assertEquals(409, cadastrarUsuarioSite("ana@x.com").codigo(), "e-mail duplicado");
        assertEquals(400, ctx.usuarioController.cadastrar(Requisicao.de("nome", "A")).codigo(), "campo ausente");
        assertEquals(400, ctx.usuarioController.cadastrar(Requisicao.de("nome", "A", "email", "a@x.com", "senha", "senha123", "tipo", "XYZ")).codigo());
        assertEquals(404, ctx.usuarioController.buscar(Requisicao.de("id", "999")).codigo());
        assertEquals(400, ctx.usuarioController.buscar(Requisicao.de("id", "abc")).codigo());
        assertEquals(422, ctx.usuarioController.autenticar(Requisicao.de("email", "ana@x.com", "senha", "errada")).codigo());
        assertEquals(200, ctx.usuarioController.autenticar(Requisicao.de("email", "ana@x.com", "senha", "senha123")).codigo());
        assertTrue(Resposta.ok("x", null).sucesso());
        assertFalse(Resposta.erro(404, "x").sucesso());
    }

    @Test
    void crudCompletoDeUsuarioPeloController() {
        Resposta criado = cadastrarUsuarioSite("ana@x.com");
        String id = String.valueOf(idDe(criado));
        assertEquals(200, ctx.usuarioController.buscar(Requisicao.de("id", id)).codigo());
        assertEquals(1, ((List<?>) ctx.usuarioController.listar(Requisicao.de("tipo", "USUARIO_SITE")).dados()).size());
        assertEquals(0, ((List<?>) ctx.usuarioController.listar(Requisicao.de("tipo", "DOADOR")).dados()).size());
        Resposta up = ctx.usuarioController.atualizar(Requisicao.de("id", id, "nome", "Ana Maria", "ativo", "false"));
        assertEquals(200, up.codigo());
        assertTrue(up.dados().toString().contains("Ana Maria") && up.dados().toString().contains("ativo=false"));
        assertFalse(up.dados().toString().contains("pbkdf2"), "o hash não pode vazar na resposta");
        assertEquals(200, ctx.usuarioController.remover(Requisicao.de("id", id)).codigo());
        assertEquals(404, ctx.usuarioController.remover(Requisicao.de("id", id)).codigo());
    }

    @Test
    void cicloDeAcervoEEmprestimoPelosControllers() {
        long usuario = idDe(cadastrarUsuarioSite("ana@x.com"));
        Resposta livro = ctx.acervoController.cadastrarLivro(Requisicao.de("titulo", "Dom Casmurro", "autor", "Machado",
                "categoria", "didatico", "anoPublicacao", "1899", "isbn", "111"));
        assertEquals(201, livro.codigo());
        long livroId = idDe(livro);
        assertEquals(409, ctx.acervoController.cadastrarLivro(Requisicao.de("titulo", "Outro", "autor", "X",
                "categoria", "OUTRO", "anoPublicacao", "2000", "isbn", "111")).codigo());

        Resposta item = ctx.acervoController.cadastrarItem(Requisicao.de("livroId", String.valueOf(livroId),
                "tipo", "EXEMPLAR_FISICO", "codigoTombo", "T1", "estado", "BOM", "localizacao", "E1"));
        assertEquals(201, item.codigo());
        assertEquals(400, ctx.acervoController.cadastrarItem(Requisicao.de("livroId", String.valueOf(livroId),
                "tipo", "EXEMPLAR_FISICO", "codigoTombo", "T2", "estado", "BOM")).codigo(), "falta localização");
        long itemId = idDe(item);
        assertEquals(1, ((List<?>) ctx.acervoController.consultarDisponiveis(Requisicao.de("titulo", "casmurro")).dados()).size());

        Resposta emprestimo = ctx.emprestimoController.emprestar(Requisicao.de("usuarioId", String.valueOf(usuario), "itemId", String.valueOf(itemId)));
        assertEquals(201, emprestimo.codigo());
        assertEquals(422, ctx.emprestimoController.emprestar(Requisicao.de("usuarioId", String.valueOf(usuario), "itemId", String.valueOf(itemId))).codigo());
        long emprestimoId = idDe(emprestimo);
        assertEquals(200, ctx.emprestimoController.renovar(Requisicao.de("id", String.valueOf(emprestimoId))).codigo());
        relogio.avancarDias(40);
        Resposta devolucao = ctx.emprestimoController.devolver(Requisicao.de("id", String.valueOf(emprestimoId)));
        assertEquals(200, devolucao.codigo());
        assertTrue(devolucao.mensagem().contains("multa"));

        long multaId = ((List<?>) ctx.multaController.listarPendentes(Requisicao.de("usuarioId", String.valueOf(usuario))).dados())
                .stream().map(m -> ((br.com.bibliotecasolidaria.model.Multa) m).getId()).findFirst().orElseThrow();
        assertEquals(200, ctx.emprestimoController.pagarMulta(Requisicao.de("id", String.valueOf(multaId))).codigo());
        assertEquals(422, ctx.emprestimoController.pagarMulta(Requisicao.de("id", String.valueOf(multaId))).codigo());
        assertEquals(409, ctx.emprestimoController.remover(Requisicao.de("id", String.valueOf(emprestimoId))).codigo(),
                "a multa referencia o empréstimo, que não pode ser removido");
    }

    @Test
    void doacaoPeloControllerResponde201ComMensagemDeAceiteOuRecusa() {
        long doador = idDe(ctx.usuarioController.cadastrar(Requisicao.de("nome", "D", "email", "d@x.com", "senha", "senha123",
                "tipo", "DOADOR", "documento", "123", "pessoaJuridica", "sim")));
        Resposta aceita = ctx.doacaoController.registrar(Requisicao.de("idDoador", String.valueOf(doador), "tipo", "DINHEIRO",
                "valor", "150,00", "meioPagamento", "PIX"));
        assertEquals(201, aceita.codigo());
        assertTrue(aceita.mensagem().contains("aceita"));
        Resposta recusada = ctx.doacaoController.registrar(Requisicao.de("idDoador", String.valueOf(doador), "tipo", "LIVRO_FISICO",
                "titulo", "T", "autor", "A", "isbn", "1", "categoria", "OUTRO", "estado", "RUIM", "quantidade", "1"));
        assertTrue(recusada.mensagem().contains("recusada"));
        assertEquals(400, ctx.doacaoController.registrar(Requisicao.de("idDoador", String.valueOf(doador), "tipo", "DINHEIRO")).codigo());
        assertEquals(404, ctx.doacaoController.registrar(Requisicao.de("idDoador", "999", "tipo", "DINHEIRO", "valor", "5", "meioPagamento", "PIX")).codigo());
        assertEquals(2, ((List<?>) ctx.doacaoController.listarPorDoador(Requisicao.de("idDoador", String.valueOf(doador))).dados()).size());
        assertEquals(1, ((List<?>) ctx.doacaoController.listar(Requisicao.de("status", "RECUSADA")).dados()).size());
        assertEquals(1, ((List<?>) ctx.bonificacaoController.listarPorDoador(Requisicao.de("doadorId", String.valueOf(doador))).dados()).size());
    }

    @Test
    void relatorioEVerificacaoDeVencimentosPelosControllers() {
        assertEquals(200, ctx.relatorioController.emprestimosPorPeriodo(Requisicao.de("inicio", "2026-01-01", "fim", "2026-12-31")).codigo());
        assertEquals(400, ctx.relatorioController.doacoesPorPeriodo(Requisicao.de("inicio", "2026-12-31", "fim", "2026-01-01")).codigo());
        assertEquals(400, ctx.relatorioController.doacoesPorPeriodo(Requisicao.de("inicio", "01/01/2026", "fim", "2026-01-01")).codigo());
        assertEquals(200, ctx.notificacaoController.verificarVencimentos(Requisicao.de()).codigo());
        assertEquals(200, ctx.emprestimoController.sincronizarAtrasos(Requisicao.de()).codigo());
    }

    @Test
    void reservaEProximaDaFilaPeloController() {
        long ana = idDe(cadastrarUsuarioSite("ana@x.com"));
        long beto = idDe(cadastrarUsuarioSite("beto@x.com"));
        long livro = novoLivro("T", "1").getId();
        long item = novoExemplar(livro, "T1").getId();
        assertEquals(404, ctx.reservaController.proximaDaFila(Requisicao.de("livroId", String.valueOf(livro))).codigo());
        assertEquals(422, ctx.reservaController.criar(Requisicao.de("usuarioId", String.valueOf(ana), "livroId", String.valueOf(livro))).codigo(), "há item disponível");
        ctx.emprestimoController.emprestar(Requisicao.de("usuarioId", String.valueOf(ana), "itemId", String.valueOf(item)));
        Resposta reserva = ctx.reservaController.criar(Requisicao.de("usuarioId", String.valueOf(beto), "livroId", String.valueOf(livro)));
        assertEquals(201, reserva.codigo());
        assertEquals(200, ctx.reservaController.proximaDaFila(Requisicao.de("livroId", String.valueOf(livro))).codigo());
        assertEquals(200, ctx.reservaController.cancelar(Requisicao.de("id", String.valueOf(idDe(reserva)))).codigo());
        assertEquals(422, ctx.reservaController.cancelar(Requisicao.de("id", String.valueOf(idDe(reserva)))).codigo());
        assertEquals(200, ctx.reservaController.remover(Requisicao.de("id", String.valueOf(idDe(reserva)))).codigo());
    }
}
