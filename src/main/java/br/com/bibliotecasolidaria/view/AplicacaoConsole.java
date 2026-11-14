package br.com.bibliotecasolidaria.view;

import br.com.bibliotecasolidaria.config.ContextoAplicacao;
import br.com.bibliotecasolidaria.controller.Requisicao;
import br.com.bibliotecasolidaria.controller.Resposta;
import br.com.bibliotecasolidaria.model.enums.CategoriaLivro;
import br.com.bibliotecasolidaria.model.enums.EstadoConservacao;
import br.com.bibliotecasolidaria.model.enums.StatusDoacao;
import br.com.bibliotecasolidaria.model.enums.StatusItem;
import br.com.bibliotecasolidaria.model.enums.TipoBonificacao;
import br.com.bibliotecasolidaria.model.enums.TipoDoacao;
import br.com.bibliotecasolidaria.model.enums.TipoEvento;
import br.com.bibliotecasolidaria.model.enums.TipoItemAcervo;
import br.com.bibliotecasolidaria.model.enums.TipoUsuario;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static br.com.bibliotecasolidaria.view.Campo.booleano;
import static br.com.bibliotecasolidaria.view.Campo.data;
import static br.com.bibliotecasolidaria.view.Campo.enumerado;
import static br.com.bibliotecasolidaria.view.Campo.obrigatorio;
import static br.com.bibliotecasolidaria.view.Campo.opcional;

/** Menu de console: cada opção pergunta os campos, chama o controller e exibe a {@link Resposta}. */
public class AplicacaoConsole {
    private final ConsoleView view;
    private final Map<String, List<OpcaoMenu>> modulos = new LinkedHashMap<>();

    public AplicacaoConsole(ConsoleView view, ContextoAplicacao ctx) {
        this.view = view;
        montarModulos(ctx);
    }

    public void executar() {
        view.mensagem("Biblioteca Solidária");
        List<String> nomes = List.copyOf(modulos.keySet());
        try {
            while (true) {
                int escolha = view.menu("Menu principal", nomes, "Sair");
                if (escolha == 0) {
                    view.mensagem("Até logo!");
                    return;
                }
                executarModulo(nomes.get(escolha - 1));
            }
        } catch (NoSuchElementException fimDaEntrada) {
            view.mensagem("\nEntrada encerrada.");
        }
    }

    private void executarModulo(String nome) {
        List<OpcaoMenu> opcoes = modulos.get(nome);
        List<String> titulos = opcoes.stream().map(OpcaoMenu::titulo).toList();
        while (true) {
            int escolha = view.menu(nome, titulos, "Voltar");
            if (escolha == 0) {
                return;
            }
            OpcaoMenu opcao = opcoes.get(escolha - 1);
            Requisicao req = view.perguntar(opcao.campos());
            view.exibir(opcao.acao().apply(req));
        }
    }

    private void montarModulos(ContextoAplicacao c) {
        var usuarios = c.usuarioController;
        var acervo = c.acervoController;
        var emprestimos = c.emprestimoController;
        var multas = c.multaController;
        var reservas = c.reservaController;
        var doacoes = c.doacaoController;
        var bonus = c.bonificacaoController;
        var notif = c.notificacaoController;
        var relatorios = c.relatorioController;

        Campo id = obrigatorio("id");
        Campo[] atributosUsuario = {opcional("documento"), booleano("pessoaJuridica", false), opcional("telefone"),
                opcional("endereco"), opcional("matricula"), opcional("cargo")};
        Campo[] dadosDoacao = {opcional("titulo"), opcional("autor"), opcional("isbn"),
                enumerado("categoria", CategoriaLivro.class, false), enumerado("estado", EstadoConservacao.class, false),
                opcional("quantidade"), opcional("arquivoUrl"), opcional("formato"), opcional("valor"),
                opcional("meioPagamento"), opcional("comprovante")};

        modulos.put("Usuários", List.of(
                new OpcaoMenu("Cadastrar usuário", usuarios::cadastrar, concat(new Campo[]{obrigatorio("nome"),
                        obrigatorio("email"), obrigatorio("senha"), enumerado("tipo", TipoUsuario.class, true)}, atributosUsuario)),
                new OpcaoMenu("Autenticar", usuarios::autenticar, obrigatorio("email"), obrigatorio("senha")),
                new OpcaoMenu("Buscar por id", usuarios::buscar, id),
                new OpcaoMenu("Listar", usuarios::listar, enumerado("tipo", TipoUsuario.class, false)),
                new OpcaoMenu("Atualizar (deixe em branco o que não muda)", usuarios::atualizar, concat(new Campo[]{id,
                        opcional("nome"), opcional("email"), opcional("senha"), booleano("ativo", false)}, atributosUsuario)),
                new OpcaoMenu("Remover", usuarios::remover, id)));

        modulos.put("Acervo - livros", List.of(
                new OpcaoMenu("Cadastrar livro", acervo::cadastrarLivro, obrigatorio("titulo"), obrigatorio("autor"),
                        opcional("editora"), opcional("isbn"), enumerado("categoria", CategoriaLivro.class, true),
                        obrigatorio("anoPublicacao")),
                new OpcaoMenu("Consultar ISBN no acervo global", acervo::preencherCadastro, obrigatorio("isbn")),
                new OpcaoMenu("Buscar livro por id", acervo::buscarLivro, id),
                new OpcaoMenu("Listar/pesquisar livros", acervo::listarLivros, opcional("titulo"),
                        enumerado("categoria", CategoriaLivro.class, false), opcional("isbn")),
                new OpcaoMenu("Atualizar livro", acervo::atualizarLivro, id, opcional("titulo"), opcional("autor"),
                        opcional("editora"), opcional("isbn"), enumerado("categoria", CategoriaLivro.class, false),
                        opcional("anoPublicacao")),
                new OpcaoMenu("Remover livro", acervo::removerLivro, id)));

        modulos.put("Acervo - itens", List.of(
                new OpcaoMenu("Cadastrar item (exemplar ou e-book)", acervo::cadastrarItem, obrigatorio("livroId"),
                        enumerado("tipo", TipoItemAcervo.class, true), opcional("codigoTombo"),
                        enumerado("estado", EstadoConservacao.class, false), opcional("localizacao"),
                        opcional("arquivoUrl"), opcional("formato")),
                new OpcaoMenu("Consultar itens disponíveis", acervo::consultarDisponiveis, opcional("titulo"),
                        enumerado("categoria", CategoriaLivro.class, false), enumerado("tipo", TipoItemAcervo.class, false)),
                new OpcaoMenu("Buscar item por id", acervo::buscarItem, id),
                new OpcaoMenu("Listar itens", acervo::listarItens, opcional("livroId")),
                new OpcaoMenu("Atualizar item", acervo::atualizarItem, id, opcional("livroId"),
                        enumerado("status", StatusItem.class, false), opcional("codigoTombo"),
                        enumerado("estado", EstadoConservacao.class, false), opcional("localizacao"),
                        opcional("arquivoUrl"), opcional("formato")),
                new OpcaoMenu("Remover item", acervo::removerItem, id)));

        modulos.put("Empréstimos", List.of(
                new OpcaoMenu("Emprestar", emprestimos::emprestar, obrigatorio("usuarioId"), obrigatorio("itemId")),
                new OpcaoMenu("Devolver", emprestimos::devolver, id),
                new OpcaoMenu("Renovar", emprestimos::renovar, id),
                new OpcaoMenu("Pagar multa", emprestimos::pagarMulta, id),
                new OpcaoMenu("Buscar por id", emprestimos::buscar, id),
                new OpcaoMenu("Listar todos", emprestimos::listar),
                new OpcaoMenu("Listar por usuário", emprestimos::listarPorUsuario, obrigatorio("usuarioId")),
                new OpcaoMenu("Listar atrasados", emprestimos::listarAtrasados),
                new OpcaoMenu("Ajustar prazo", emprestimos::ajustarPrazo, id, data("dataPrevistaDevolucao", true)),
                new OpcaoMenu("Marcar atrasados (sincronizar)", emprestimos::sincronizarAtrasos),
                new OpcaoMenu("Remover (só devolvidos)", emprestimos::remover, id)));

        modulos.put("Multas", List.of(
                new OpcaoMenu("Criar multa manual", multas::criar, obrigatorio("emprestimoId"), obrigatorio("valor")),
                new OpcaoMenu("Buscar por id", multas::buscar, id),
                new OpcaoMenu("Listar todas", multas::listar),
                new OpcaoMenu("Listar pendentes de um usuário", multas::listarPendentes, obrigatorio("usuarioId")),
                new OpcaoMenu("Alterar valor (pendentes)", multas::atualizar, id, obrigatorio("valor")),
                new OpcaoMenu("Remover/perdoar (pendentes)", multas::remover, id)));

        modulos.put("Reservas", List.of(
                new OpcaoMenu("Reservar livro", reservas::criar, obrigatorio("usuarioId"), obrigatorio("livroId")),
                new OpcaoMenu("Buscar por id", reservas::buscar, id),
                new OpcaoMenu("Listar todas", reservas::listar),
                new OpcaoMenu("Listar por usuário", reservas::listarPorUsuario, obrigatorio("usuarioId")),
                new OpcaoMenu("Próxima da fila de um livro", reservas::proximaDaFila, obrigatorio("livroId")),
                new OpcaoMenu("Cancelar", reservas::cancelar, id),
                new OpcaoMenu("Marcar como atendida", reservas::atender, id),
                new OpcaoMenu("Remover", reservas::remover, id)));

        modulos.put("Doações", List.of(
                new OpcaoMenu("Registrar e processar doação", doacoes::registrar, concat(new Campo[]{
                        obrigatorio("idDoador"), enumerado("tipo", TipoDoacao.class, true)}, dadosDoacao)),
                new OpcaoMenu("Reavaliar doação pendente", doacoes::avaliar, id),
                new OpcaoMenu("Aceitar manualmente", doacoes::aceitar, id),
                new OpcaoMenu("Recusar", doacoes::recusar, id, obrigatorio("motivo")),
                new OpcaoMenu("Buscar por id", doacoes::buscar, id),
                new OpcaoMenu("Listar", doacoes::listar, enumerado("status", StatusDoacao.class, false)),
                new OpcaoMenu("Listar por doador", doacoes::listarPorDoador, obrigatorio("idDoador")),
                new OpcaoMenu("Atualizar (não finalizadas)", doacoes::atualizar, concat(new Campo[]{id}, dadosDoacao)),
                new OpcaoMenu("Remover (não aceitas)", doacoes::remover, id)));

        modulos.put("Bonificações", List.of(
                new OpcaoMenu("Conceder manualmente", bonus::conceder, obrigatorio("doadorId"),
                        enumerado("tipo", TipoBonificacao.class, true), obrigatorio("descricao")),
                new OpcaoMenu("Buscar por id", bonus::buscar, id),
                new OpcaoMenu("Listar todas", bonus::listar),
                new OpcaoMenu("Listar por doador", bonus::listarPorDoador, obrigatorio("doadorId")),
                new OpcaoMenu("Atualizar", bonus::atualizar, id, enumerado("tipo", TipoBonificacao.class, false),
                        opcional("descricao")),
                new OpcaoMenu("Remover", bonus::remover, id)));

        modulos.put("Notificações", List.of(
                new OpcaoMenu("Criar notificação", notif::criar, obrigatorio("usuarioId"),
                        enumerado("tipo", TipoEvento.class, true), obrigatorio("mensagem")),
                new OpcaoMenu("Buscar por id", notif::buscar, id),
                new OpcaoMenu("Listar todas", notif::listar),
                new OpcaoMenu("Listar por usuário", notif::listarPorUsuario, obrigatorio("usuarioId"),
                        booleano("apenasNaoLidas", false)),
                new OpcaoMenu("Marcar como lida", notif::marcarComoLida, id),
                new OpcaoMenu("Alterar mensagem", notif::atualizar, id, obrigatorio("mensagem")),
                new OpcaoMenu("Remover", notif::remover, id),
                new OpcaoMenu("Executar verificação de vencimentos", notif::verificarVencimentos)));

        modulos.put("Relatórios", List.of(
                new OpcaoMenu("Empréstimos por período", relatorios::emprestimosPorPeriodo, data("inicio", true),
                        data("fim", true)),
                new OpcaoMenu("Doações por período", relatorios::doacoesPorPeriodo, data("inicio", true),
                        data("fim", true))));
    }

    private static Campo[] concat(Campo[] a, Campo[] b) {
        Campo[] r = new Campo[a.length + b.length];
        System.arraycopy(a, 0, r, 0, a.length);
        System.arraycopy(b, 0, r, a.length, b.length);
        return r;
    }
}
