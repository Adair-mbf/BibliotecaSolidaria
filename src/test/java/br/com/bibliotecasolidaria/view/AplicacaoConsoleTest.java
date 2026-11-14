package br.com.bibliotecasolidaria.view;

import br.com.bibliotecasolidaria.support.BancoTestBase;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class AplicacaoConsoleTest extends BancoTestBase {
    private String executar(String... linhas) {
        ByteArrayOutputStream saida = new ByteArrayOutputStream();
        String entrada = String.join("\n", linhas) + "\n";
        new AplicacaoConsole(new ConsoleView(new ByteArrayInputStream(entrada.getBytes(StandardCharsets.UTF_8)),
                new PrintStream(saida, true, StandardCharsets.UTF_8)), ctx).executar();
        return saida.toString(StandardCharsets.UTF_8);
    }

    @Test
    void cadastraEListaLivroPeloMenu() {
        String out = executar("2", "1", "Livro X", "Autor Y", "", "", "DIDATICO", "2001", // cadastrar livro
                "4", "", "", "", // listar livros sem filtros
                "0", "0");
        assertTrue(out.contains("[OK 201] Livro cadastrado."), out);
        assertTrue(out.contains("1 livro(s) encontrado(s)."), out);
        assertTrue(out.contains("Livro X"), out);
        assertTrue(out.contains("Até logo!"));
        assertEquals(1, ctx.acervoService.listarLivros().size());
    }

    @Test
    void mostraErrosDeValidacaoSemEncerrar() {
        String out = executar("1", "1", "Ana", "email-invalido", "senha123", "USUARIO_SITE", "", "", "", "", "", "", "0", "0");
        assertTrue(out.contains("[ERRO 400]"), out);
        assertTrue(ctx.usuarioService.listar().isEmpty());
    }

    @Test
    void rejeitaOpcaoInvalidaEPermiteContinuar() {
        String out = executar("abc", "99", "0");
        assertEquals(2, out.split("Opção inválida.", -1).length - 1, out);
        assertTrue(out.contains("Até logo!"));
    }

    @Test
    void encerraSemErroQuandoAEntradaAcaba() {
        String out = executar("1");
        assertTrue(out.contains("Entrada encerrada."), out);
    }

    @Test
    void listaRegistrosVaziosComMensagemAmigavel() {
        String out = executar("4", "6", "0", "0");
        assertTrue(out.contains("0 empréstimo(s) encontrado(s).") && out.contains("(nenhum registro)"), out);
        String out2 = executar("5", "3", "0", "0");
        assertTrue(out2.contains("0 multa(s) encontrado(s).") && out2.contains("(nenhum registro)"), out2);
    }
}
