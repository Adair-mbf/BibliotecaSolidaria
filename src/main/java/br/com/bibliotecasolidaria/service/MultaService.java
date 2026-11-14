package br.com.bibliotecasolidaria.service;

import br.com.bibliotecasolidaria.adapter.GatewayPagamento;
import br.com.bibliotecasolidaria.exception.ConflitoException;
import br.com.bibliotecasolidaria.exception.NaoEncontradoException;
import br.com.bibliotecasolidaria.exception.NegocioException;
import br.com.bibliotecasolidaria.exception.ValidacaoException;
import br.com.bibliotecasolidaria.model.ComprovantePagamento;
import br.com.bibliotecasolidaria.model.Emprestimo;
import br.com.bibliotecasolidaria.model.Multa;
import br.com.bibliotecasolidaria.model.Usuario;
import br.com.bibliotecasolidaria.model.enums.StatusMulta;
import br.com.bibliotecasolidaria.persistence.MultaRepositorio;
import br.com.bibliotecasolidaria.strategy.CalculoMultaStrategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

/** Context do Strategy de cálculo e cliente do Adapter de pagamento. */
public class MultaService {
    private final MultaRepositorio multas;
    private final EmprestimoService emprestimos;
    private final UsuarioService usuarios;
    private final GatewayPagamento gateway;
    private final Clock clock;
    private CalculoMultaStrategy estrategia;

    public MultaService(MultaRepositorio multas, EmprestimoService emprestimos, UsuarioService usuarios,
                        GatewayPagamento gateway, CalculoMultaStrategy estrategia, Clock clock) {
        this.multas = multas;
        this.emprestimos = emprestimos;
        this.usuarios = usuarios;
        this.gateway = gateway;
        this.estrategia = estrategia;
        this.clock = clock;
    }

    public void definirEstrategia(CalculoMultaStrategy estrategia) {
        this.estrategia = estrategia;
    }

    /** Gera a multa do empréstimo (idempotente: devolve a existente, se houver). Exige atraso. */
    public Multa gerarMulta(Emprestimo emprestimo) {
        LocalDate hoje = LocalDate.now(clock);
        int dias = emprestimo.diasAtraso(hoje);
        if (dias <= 0) {
            throw new NegocioException("O empréstimo " + emprestimo.getId() + " não está em atraso.");
        }
        return multas.buscarPorEmprestimo(emprestimo.getId()).orElseGet(() -> multas.salvar(
                new Multa(null, emprestimo.getId(), estrategia.calcular(dias, emprestimo), hoje, null,
                        StatusMulta.PENDENTE)));
    }

    /** Cobra a multa pelo gateway; ela só é quitada se o pagamento for aprovado. */
    public ComprovantePagamento pagarMulta(long idMulta) {
        Multa multa = buscar(idMulta);
        if (multa.getStatus() == StatusMulta.PAGA) {
            throw new NegocioException("A multa " + idMulta + " já foi paga.");
        }
        Usuario pagador = usuarios.buscar(emprestimos.buscar(multa.getEmprestimoId()).getUsuarioId());
        ComprovantePagamento comprovante = gateway.cobrar(multa, pagador);
        if (comprovante.aprovado()) {
            multa.quitar(LocalDate.now(clock));
            multas.salvar(multa);
        }
        return comprovante;
    }

    public List<Multa> listarPendentes(long usuarioId) { return multas.listarPendentes(usuarioId); }

    public boolean possuiPendencias(long usuarioId) { return !multas.listarPendentes(usuarioId).isEmpty(); }

    public Multa buscar(long id) {
        return multas.buscarPorId(id).orElseThrow(() -> new NaoEncontradoException("Multa " + id + " não encontrada."));
    }

    public List<Multa> listar() { return multas.listarTodos(); }

    /** Criação manual por um funcionário, com valor livre. */
    public Multa criar(long emprestimoId, BigDecimal valor) {
        emprestimos.buscar(emprestimoId);
        validarValor(valor);
        if (multas.buscarPorEmprestimo(emprestimoId).isPresent()) {
            throw new ConflitoException("O empréstimo " + emprestimoId + " já possui multa.");
        }
        return multas.salvar(new Multa(null, emprestimoId, valor.setScale(2, RoundingMode.HALF_UP),
                LocalDate.now(clock), null, StatusMulta.PENDENTE));
    }

    /** Altera o valor de uma multa ainda pendente (ex.: abatimento). */
    public Multa atualizarValor(long id, BigDecimal novoValor) {
        Multa m = buscar(id);
        if (m.getStatus() != StatusMulta.PENDENTE) {
            throw new NegocioException("Só é possível alterar multas pendentes.");
        }
        validarValor(novoValor);
        m.setValor(novoValor.setScale(2, RoundingMode.HALF_UP));
        return multas.salvar(m);
    }

    /** Cancela (perdoa) uma multa pendente. */
    public void remover(long id) {
        Multa m = buscar(id);
        if (m.getStatus() != StatusMulta.PENDENTE) {
            throw new NegocioException("Multas pagas não podem ser removidas.");
        }
        multas.remover(id);
    }

    private static void validarValor(BigDecimal valor) {
        if (valor == null || valor.signum() <= 0) {
            throw new ValidacaoException("O valor da multa deve ser maior que zero.");
        }
    }
}
