package br.com.bibliotecasolidaria;

import br.com.bibliotecasolidaria.config.ConfiguracaoSistema;
import br.com.bibliotecasolidaria.config.ContextoAplicacao;
import br.com.bibliotecasolidaria.persistence.ConexaoBanco;
import br.com.bibliotecasolidaria.persistence.InicializadorSchema;
import br.com.bibliotecasolidaria.view.AplicacaoConsole;
import br.com.bibliotecasolidaria.view.ConsoleView;

import java.util.Arrays;

/**
 * Ponto de entrada. Variáveis de ambiente: DB_URL, DB_USER, DB_PASSWORD.
 * Use o argumento {@code --criar-schema} para criar as tabelas em um banco vazio.
 */
public final class Main {
    private Main() { }

    public static void main(String[] args) {
        ConexaoBanco conexao;
        try {
            conexao = ConexaoBanco.getInstancia();
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.err.println("Defina DB_URL, DB_USER e DB_PASSWORD (veja o README).");
            System.exit(1);
            return;
        }
        try {
            if (Arrays.asList(args).contains("--criar-schema")) {
                InicializadorSchema.executar(conexao);
                System.out.println("Schema criado.");
            }
            ContextoAplicacao contexto = ContextoAplicacao.criar(conexao, ConfiguracaoSistema.getInstancia());
            new AplicacaoConsole(new ConsoleView(System.in, System.out), contexto).executar();
        } finally {
            conexao.fechar();
        }
    }
}
