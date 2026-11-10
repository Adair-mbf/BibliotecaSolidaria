package br.com.bibliotecasolidaria;

import br.com.bibliotecasolidaria.controller.LivroController;
import br.com.bibliotecasolidaria.persistence.ConnectionFactory;
import br.com.bibliotecasolidaria.persistence.LivroDao;
import br.com.bibliotecasolidaria.view.ConsoleView;
import br.com.bibliotecasolidaria.view.ConsoleView.LivroDados;

import java.sql.SQLException;

/** Ponto de entrada: monta as dependências MVC e inicia o CRUD de livros. */
public final class Main {
    private Main() { }

    public static void main(String[] args) {
        try (var view = new ConsoleView()) {
            var controller = new LivroController(new LivroDao(ConnectionFactory.fromEnvironment()));
            executarMenu(view, controller);
        } catch (IllegalArgumentException exception) {
            System.err.println("Configuração inválida: " + exception.getMessage());
        }
    }

    private static void executarMenu(ConsoleView view, LivroController controller) {
        boolean executando = true;
        while (executando) {
            try {
                switch (view.menu()) {
                    case 1 -> criar(view, controller);
                    case 2 -> view.mostrarTodos(controller.listar());
                    case 3 -> buscar(view, controller);
                    case 4 -> atualizar(view, controller);
                    case 5 -> remover(view, controller);
                    case 0 -> executando = false;
                    default -> view.mensagem("Opção inválida.");
                }
            } catch (SQLException | IllegalArgumentException exception) {
                view.mensagem("Não foi possível concluir a operação: " + exception.getMessage());
            }
        }
    }

    private static void criar(ConsoleView v, LivroController c) throws SQLException {
        LivroDados d = v.lerLivro();
        v.mensagem("Livro cadastrado com ID " + c.criar(d.titulo(), d.autor(), d.editora(), d.isbn(), d.categoria(), d.anoPublicacao()).id());
    }

    private static void buscar(ConsoleView v, LivroController c) throws SQLException {
        c.buscar(v.lerId()).ifPresentOrElse(v::mostrar, () -> v.mensagem("Livro não encontrado."));
    }

    private static void atualizar(ConsoleView v, LivroController c) throws SQLException {
        long id = v.lerId(); LivroDados d = v.lerLivro();
        v.mensagem(c.atualizar(id, d.titulo(), d.autor(), d.editora(), d.isbn(), d.categoria(), d.anoPublicacao())
                ? "Livro atualizado." : "Livro não encontrado.");
    }

    private static void remover(ConsoleView v, LivroController c) throws SQLException {
        v.mensagem(c.remover(v.lerId()) ? "Livro removido." : "Livro não encontrado.");
    }
}
