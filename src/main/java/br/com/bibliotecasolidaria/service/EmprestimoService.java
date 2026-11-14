package br.com.bibliotecasolidaria.service;

import br.com.bibliotecasolidaria.config.ConfiguracaoSistema;
import br.com.bibliotecasolidaria.exception.NaoEncontradoException;
import br.com.bibliotecasolidaria.exception.NegocioException;
import br.com.bibliotecasolidaria.exception.ValidacaoException;
import br.com.bibliotecasolidaria.model.Emprestimo;
import br.com.bibliotecasolidaria.model.ItemAcervo;
import br.com.bibliotecasolidaria.model.UsuarioSite;
import br.com.bibliotecasolidaria.model.enums.StatusEmprestimo;
import br.com.bibliotecasolidaria.persistence.EmprestimoRepositorio;
import br.com.bibliotecasolidaria.strategy.PoliticaRenovacao;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

public class EmprestimoService {
    private final EmprestimoRepositorio emprestimos;
    private final AcervoService acervo;
    private final ReservaService reservas;
    private final ConfiguracaoSistema config;
    private final PoliticaRenovacao politicaRenovacao;
    private final Clock clock;

    public EmprestimoService(EmprestimoRepositorio emprestimos, AcervoService acervo, ReservaService reservas,
                             ConfiguracaoSistema config, PoliticaRenovacao politicaRenovacao, Clock clock) {
        this.emprestimos = emprestimos;
        this.acervo = acervo;
        this.reservas = reservas;
        this.config = config;
        this.politicaRenovacao = politicaRenovacao;
        this.clock = clock;
    }

    /** Registra o empréstimo e marca o item como emprestado. As pré-condições são verificadas pela facade. */
    public Emprestimo registrarEmprestimo(UsuarioSite usuario, ItemAcervo item) {
        LocalDate hoje = LocalDate.now(clock);
        Emprestimo e = emprestimos.salvar(new Emprestimo(null, usuario.getId(), item.getId(), hoje,
                hoje.plusDays(config.getDiasEmprestimo()), null, 0, StatusEmprestimo.ATIVO));
        acervo.marcarEmprestado(item.getId());
        return e;
    }

    /** Registra a devolução, libera o item (ou o segura para o primeiro da fila de reservas) e devolve o empréstimo. */
    public Emprestimo registrarDevolucao(long id) {
        Emprestimo e = buscar(id);
        e.registrarDevolucao(LocalDate.now(clock));
        emprestimos.salvar(e);
        ItemAcervo item = acervo.buscarItem(e.getItemAcervoId());
        if (!reservas.reservarItemParaProximo(item)) {
            acervo.marcarDisponivel(item.getId());
        }
        return e;
    }

    public Emprestimo renovar(long id) {
        Emprestimo e = buscar(id);
        if (!politicaRenovacao.podeRenovar(e)) {
            throw new NegocioException("Este empréstimo não pode ser renovado (limite atingido, atrasado ou já devolvido).");
        }
        ItemAcervo item = acervo.buscarItem(e.getItemAcervoId());
        if (reservas.existeFilaParaLivro(item.getLivroId())) {
            throw new NegocioException("Há reservas ativas para este livro; não é possível renovar.");
        }
        e.renovar(politicaRenovacao.calcularNovoPrazo(e));
        return emprestimos.salvar(e);
    }

    /** Marca como ATRASADO os empréstimos ativos com prazo vencido. Retorna quantos foram alterados. */
    public int atualizarStatusAtrasados() {
        int alterados = 0;
        for (Emprestimo e : emprestimos.listarAtrasados(LocalDate.now(clock))) {
            if (e.getStatus() == StatusEmprestimo.ATIVO) {
                e.marcarAtrasado();
                emprestimos.salvar(e);
                alterados++;
            }
        }
        return alterados;
    }

    public Emprestimo buscar(long id) {
        return emprestimos.buscarPorId(id).orElseThrow(() -> new NaoEncontradoException("Empréstimo " + id + " não encontrado."));
    }

    public List<Emprestimo> listar() { return emprestimos.listarTodos(); }

    public List<Emprestimo> listarPorUsuario(long usuarioId) { return emprestimos.listarPorUsuario(usuarioId); }

    public List<Emprestimo> listarAtrasados() { return emprestimos.listarAtrasados(LocalDate.now(clock)); }

    public List<Emprestimo> listarEmAbertoComVencimentoEntre(LocalDate ini, LocalDate fim) {
        return emprestimos.listarEmAbertoComVencimentoEntre(ini, fim);
    }

    /** Correção administrativa do prazo de um empréstimo em aberto. */
    public Emprestimo ajustarPrazo(long id, LocalDate novoPrazo) {
        Emprestimo e = buscar(id);
        if (!e.estaEmAberto()) {
            throw new NegocioException("Só é possível ajustar o prazo de empréstimos em aberto.");
        }
        if (novoPrazo == null || novoPrazo.isBefore(e.getDataEmprestimo())) {
            throw new ValidacaoException("O prazo não pode ser anterior à data do empréstimo.");
        }
        e.setDataPrevistaDevolucao(novoPrazo);
        e.setStatus(novoPrazo.isBefore(LocalDate.now(clock)) ? StatusEmprestimo.ATRASADO : StatusEmprestimo.ATIVO);
        return emprestimos.salvar(e);
    }

    /** Remove apenas registros já devolvidos (empréstimos em aberto deixariam o item inconsistente). */
    public void remover(long id) {
        Emprestimo e = buscar(id);
        if (e.estaEmAberto()) {
            throw new NegocioException("Devolva o empréstimo antes de removê-lo.");
        }
        emprestimos.remover(id);
    }
}
