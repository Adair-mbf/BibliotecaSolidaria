package br.com.bibliotecasolidaria.view;

import br.com.bibliotecasolidaria.controller.Requisicao;
import br.com.bibliotecasolidaria.controller.Resposta;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

/** View de console: só lê entrada e imprime saída; toda regra fica nos controllers. */
public class ConsoleView {
    private final Scanner entrada;
    private final PrintStream saida;

    public ConsoleView(InputStream in, PrintStream out) {
        this.entrada = new Scanner(in);
        this.saida = out;
    }

    /** Lê uma linha; lança {@link NoSuchElementException} se a entrada acabar. */
    public String ler(String prompt) {
        saida.print(prompt);
        saida.flush();
        return entrada.nextLine().trim();
    }

    /** Mostra o menu e devolve a escolha (0 = voltar/sair). */
    public int menu(String titulo, List<String> opcoes, String rotuloZero) {
        while (true) {
            saida.println();
            saida.println("=== " + titulo + " ===");
            for (int i = 0; i < opcoes.size(); i++) {
                saida.printf("%2d - %s%n", i + 1, opcoes.get(i));
            }
            saida.printf("%2d - %s%n", 0, rotuloZero);
            String linha = ler("Opção: ");
            try {
                int n = Integer.parseInt(linha);
                if (n >= 0 && n <= opcoes.size()) {
                    return n;
                }
            } catch (NumberFormatException ignorada) {
                // cai na mensagem abaixo
            }
            saida.println("Opção inválida.");
        }
    }

    public Requisicao perguntar(List<Campo> campos) {
        Map<String, String> valores = new HashMap<>();
        for (Campo c : campos) {
            valores.put(c.nome(), ler(c.rotulo()));
        }
        return new Requisicao(valores);
    }

    public void exibir(Resposta r) {
        saida.println();
        saida.println((r.sucesso() ? "[OK " : "[ERRO ") + r.codigo() + "] " + r.mensagem());
        Object dados = r.dados();
        if (dados instanceof Iterable<?> itens) {
            Iterator<?> it = itens.iterator();
            if (!it.hasNext()) {
                saida.println("  (nenhum registro)");
            }
            while (it.hasNext()) {
                saida.println("  " + it.next());
            }
        } else if (dados != null) {
            saida.println("  " + dados);
        }
    }

    public void mensagem(String texto) {
        saida.println(texto);
    }
}
