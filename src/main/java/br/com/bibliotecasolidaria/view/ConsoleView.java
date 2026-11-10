package br.com.bibliotecasolidaria.view;

import br.com.bibliotecasolidaria.model.Domain.CategoriaLivro;
import br.com.bibliotecasolidaria.model.Domain.Livro;

import java.util.List;
import java.util.Scanner;

/** Responsável somente pela entrada e saída da aplicação de console. */
public final class ConsoleView implements AutoCloseable {
    private final Scanner scanner = new Scanner(System.in);

    public int menu() {
        System.out.println("\n=== Biblioteca Solidária ===");
        System.out.println("1 - Cadastrar livro\n2 - Listar livros\n3 - Buscar livro por ID");
        System.out.println("4 - Atualizar livro\n5 - Excluir livro\n0 - Sair");
        return inteiro("Escolha: ");
    }

    public LivroDados lerLivro() {
        return new LivroDados(texto("Título: "), texto("Autor: "), texto("Editora (opcional): "),
                texto("ISBN (opcional): "), categoria(), inteiro("Ano de publicação: "));
    }

    public long lerId() { return inteiro("ID: "); }

    public void mostrar(Livro livro) {
        System.out.printf("%d | %s | %s | %s | %d%n", livro.id(), livro.titulo(), livro.autor(),
                livro.categoria(), livro.anoPublicacao());
    }

    public void mostrarTodos(List<Livro> livros) {
        if (livros.isEmpty()) { System.out.println("Nenhum livro cadastrado."); return; }
        livros.forEach(this::mostrar);
    }

    public void mensagem(String mensagem) { System.out.println(mensagem); }

    private CategoriaLivro categoria() {
        while (true) {
            try { return CategoriaLivro.valueOf(texto("Categoria (DIDATICO, INFANTIL, OUTRO): ").toUpperCase()); }
            catch (IllegalArgumentException exception) { System.out.println("Categoria inválida."); }
        }
    }

    private String texto(String mensagem) { System.out.print(mensagem); return scanner.nextLine().trim(); }

    private int inteiro(String mensagem) {
        while (true) {
            try { return Integer.parseInt(texto(mensagem)); }
            catch (NumberFormatException exception) { System.out.println("Digite um número válido."); }
        }
    }

    @Override public void close() { scanner.close(); }

    public record LivroDados(String titulo, String autor, String editora, String isbn,
                             CategoriaLivro categoria, int anoPublicacao) { }
}
