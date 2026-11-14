package br.com.bibliotecasolidaria.service;

import br.com.bibliotecasolidaria.config.ConfiguracaoSistema;
import br.com.bibliotecasolidaria.model.Emprestimo;
import br.com.bibliotecasolidaria.model.ItemAcervo;
import br.com.bibliotecasolidaria.model.Livro;
import br.com.bibliotecasolidaria.model.enums.TipoEvento;
import br.com.bibliotecasolidaria.observer.EventoBiblioteca;
import br.com.bibliotecasolidaria.observer.NotificadorVencimento;
import br.com.bibliotecasolidaria.observer.PublicadorEventos;
import br.com.bibliotecasolidaria.persistence.NotificacaoRepositorio;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

/**
 * Tarefa de verificação de vencimentos: deve ser executada uma vez por dia (por cron, agendador do sistema
 * ou pela opção de menu). É idempotente no dia: não repete o mesmo aviso ao mesmo usuário.
 */
public class AgendadorVencimentos {
    private final EmprestimoService emprestimos;
    private final AcervoService acervo;
    private final UsuarioService usuarios;
    private final PublicadorEventos eventos;
    private final NotificacaoRepositorio notificacoes;
    private final ConfiguracaoSistema config;
    private final Clock clock;

    public AgendadorVencimentos(EmprestimoService emprestimos, AcervoService acervo, UsuarioService usuarios,
                                PublicadorEventos eventos, NotificacaoRepositorio notificacoes,
                                ConfiguracaoSistema config, Clock clock) {
        this.emprestimos = emprestimos;
        this.acervo = acervo;
        this.usuarios = usuarios;
        this.eventos = eventos;
        this.notificacoes = notificacoes;
        this.config = config;
        this.clock = clock;
    }

    /** Atualiza empréstimos atrasados e avisa os que vencem nos próximos dias. Retorna quantos avisos foram enviados. */
    public int verificarVencimentosProximos() {
        emprestimos.atualizarStatusAtrasados();
        LocalDate hoje = LocalDate.now(clock);
        int enviados = 0;
        for (Emprestimo e : emprestimos.listarEmAbertoComVencimentoEntre(hoje,
                hoje.plusDays(config.getDiasAvisoVencimento()))) {
            ItemAcervo item = acervo.buscarItem(e.getItemAcervoId());
            Livro livro = acervo.buscarLivro(item.getLivroId());
            long dias = ChronoUnit.DAYS.between(hoje, e.getDataPrevistaDevolucao());
            String mensagem = NotificadorVencimento.mensagem(livro.getTitulo(), e.getDataPrevistaDevolucao(), dias);
            if (notificacoes.existeNoDia(e.getUsuarioId(), TipoEvento.VENCIMENTO_PROXIMO, mensagem, hoje)) {
                continue;
            }
            eventos.notificar(new EventoBiblioteca(TipoEvento.VENCIMENTO_PROXIMO, usuarios.buscar(e.getUsuarioId()),
                    mensagem, Map.of("emprestimoId", e.getId(), "livroTitulo", livro.getTitulo(),
                    "prazo", e.getDataPrevistaDevolucao(), "diasRestantes", dias), LocalDateTime.now(clock)));
            enviados++;
        }
        return enviados;
    }
}
