package br.com.bibliotecasolidaria.service;

import br.com.bibliotecasolidaria.dto.DoacaoDTO;
import br.com.bibliotecasolidaria.exception.NaoEncontradoException;
import br.com.bibliotecasolidaria.exception.NegocioException;
import br.com.bibliotecasolidaria.exception.ValidacaoException;
import br.com.bibliotecasolidaria.model.Bonificacao;
import br.com.bibliotecasolidaria.model.Doacao;
import br.com.bibliotecasolidaria.model.DoacaoDinheiro;
import br.com.bibliotecasolidaria.model.DoacaoEbook;
import br.com.bibliotecasolidaria.model.DoacaoLivroFisico;
import br.com.bibliotecasolidaria.model.Doador;
import br.com.bibliotecasolidaria.model.enums.CategoriaLivro;
import br.com.bibliotecasolidaria.model.enums.EstadoConservacao;
import br.com.bibliotecasolidaria.model.enums.StatusDoacao;
import br.com.bibliotecasolidaria.model.enums.TipoEvento;
import br.com.bibliotecasolidaria.observer.EventoBiblioteca;
import br.com.bibliotecasolidaria.observer.PublicadorEventos;
import br.com.bibliotecasolidaria.persistence.DoacaoRepositorio;
import br.com.bibliotecasolidaria.util.Textos;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoacaoService {
    private final DoacaoRepositorio doacoes;
    private final UsuarioService usuarios;
    private final PublicadorEventos eventos;
    private final Clock clock;

    public DoacaoService(DoacaoRepositorio doacoes, UsuarioService usuarios, PublicadorEventos eventos, Clock clock) {
        this.doacoes = doacoes;
        this.usuarios = usuarios;
        this.eventos = eventos;
        this.clock = clock;
    }

    /** Registra a doação com status REGISTRADA após validar o doador e converter os dados do item. */
    public Doacao registrar(DoacaoDTO dto) {
        if (dto == null || dto.idDoador() == null || dto.tipo() == null) {
            throw new ValidacaoException("Informe o doador e o tipo da doação.");
        }
        Doador doador = usuarios.buscarDoador(dto.idDoador());
        if (!doador.isAtivo()) {
            throw new NegocioException("Doador inativo.");
        }
        Doacao d = switch (dto.tipo()) {
            case LIVRO_FISICO -> new DoacaoLivroFisico(null, doador.getId(), LocalDate.now(clock),
                    StatusDoacao.REGISTRADA, null, null, null, null, null, 0);
            case EBOOK -> new DoacaoEbook(null, doador.getId(), LocalDate.now(clock), StatusDoacao.REGISTRADA,
                    null, null, null, null);
            case DINHEIRO -> new DoacaoDinheiro(null, doador.getId(), LocalDate.now(clock),
                    StatusDoacao.REGISTRADA, null, null, null);
        };
        aplicarDados(d, dto.dadosItem());
        validarObrigatorios(d);
        return doacoes.salvar(d);
    }

    /** Altera dados do item de uma doação ainda não finalizada; só as chaves presentes são alteradas. */
    public Doacao atualizar(long id, Map<String, Object> dadosItem) {
        Doacao d = buscar(id);
        if (d.estaFinalizada()) {
            throw new NegocioException("Doações finalizadas não podem ser alteradas.");
        }
        aplicarDados(d, dadosItem);
        validarObrigatorios(d);
        return doacoes.salvar(d);
    }

    /** Remove uma doação que não tenha sido aceita (as aceitas já geraram itens e bonificações). */
    public void remover(long id) {
        Doacao d = buscar(id);
        if (d.getStatus() == StatusDoacao.ACEITA) {
            throw new NegocioException("Doações aceitas não podem ser removidas.");
        }
        doacoes.remover(id);
    }

    public Doacao buscar(long id) {
        return doacoes.buscarPorId(id).orElseThrow(() -> new NaoEncontradoException("Doação " + id + " não encontrada."));
    }

    public List<Doacao> listar() { return doacoes.listarTodos(); }

    public List<Doacao> listarPorDoador(long doadorId) { return doacoes.listarPorDoador(doadorId); }

    public List<Doacao> listarPorStatus(StatusDoacao status) { return doacoes.listarPorStatus(status); }

    /** Marca a doação como EM_AVALIACAO e informa se ela atende aos critérios mínimos do seu tipo. */
    public boolean avaliar(long id) {
        Doacao d = buscar(id);
        d.iniciarAvaliacao();
        doacoes.salvar(d);
        return d.avaliar();
    }

    public Doacao aceitar(long id) {
        return aceitar(id, List.of());
    }

    /** Aceita a doação e publica DOACAO_ACEITA, informando as bonificações concedidas. */
    public Doacao aceitar(long id, List<Bonificacao> bonificacoes) {
        Doacao d = buscar(id);
        d.aceitar();
        doacoes.salvar(d);
        eventos.notificar(new EventoBiblioteca(TipoEvento.DOACAO_ACEITA, usuarios.buscar(d.getDoadorId()),
                Map.of("doacaoId", d.getId(), "bonificacoes", List.copyOf(bonificacoes)), LocalDateTime.now(clock)));
        return d;
    }

    public Doacao recusar(long id, String motivo) {
        Doacao d = buscar(id);
        d.recusar(motivo);
        return doacoes.salvar(d);
    }

    // ------------------------------------------------------------ conversões

    /** Campos sem os quais a doação sequer pode ser registrada (a qualidade é julgada depois, em avaliar). */
    private static void validarObrigatorios(Doacao d) {
        if (d instanceof DoacaoLivroFisico l) {
            exigir(l.getTitulo(), "título");
            exigir(l.getAutor(), "autor");
            exigir(l.getIsbn(), "isbn");
            if (l.getCategoria() == null) throw new ValidacaoException("A categoria é obrigatória.");
            if (l.getEstado() == null) throw new ValidacaoException("O estado de conservação é obrigatório.");
            if (l.getQuantidade() <= 0) throw new ValidacaoException("A quantidade deve ser maior que zero.");
        } else if (d instanceof DoacaoEbook e) {
            exigir(e.getTitulo(), "título");
            exigir(e.getAutor(), "autor");
            exigir(e.getArquivoUrl(), "arquivoUrl");
            exigir(e.getFormato(), "formato");
        } else if (d instanceof DoacaoDinheiro m) {
            if (m.getValor() == null || m.getValor().signum() <= 0) {
                throw new ValidacaoException("O valor da doação deve ser maior que zero.");
            }
            exigir(m.getMeioPagamento(), "meioPagamento");
        }
    }

    private static void exigir(String valor, String campo) {
        if (Textos.emBranco(valor)) throw new ValidacaoException("O campo '" + campo + "' é obrigatório.");
    }

    private void aplicarDados(Doacao d, Map<String, Object> dados) {
        if (d instanceof DoacaoLivroFisico l) {
            if (dados.containsKey("titulo")) l.setTitulo(texto(dados, "titulo"));
            if (dados.containsKey("autor")) l.setAutor(texto(dados, "autor"));
            if (dados.containsKey("isbn")) l.setIsbn(texto(dados, "isbn"));
            if (dados.containsKey("categoria")) l.setCategoria(enumerado(dados, "categoria", CategoriaLivro.class));
            if (dados.containsKey("estado")) l.setEstado(enumerado(dados, "estado", EstadoConservacao.class));
            if (dados.containsKey("quantidade")) l.setQuantidade(inteiro(dados, "quantidade"));
        } else if (d instanceof DoacaoEbook e) {
            if (dados.containsKey("titulo")) e.setTitulo(texto(dados, "titulo"));
            if (dados.containsKey("autor")) e.setAutor(texto(dados, "autor"));
            if (dados.containsKey("arquivoUrl")) e.setArquivoUrl(texto(dados, "arquivoUrl"));
            if (dados.containsKey("formato")) e.setFormato(texto(dados, "formato"));
        } else if (d instanceof DoacaoDinheiro m) {
            if (dados.containsKey("valor")) m.setValor(decimal(dados, "valor"));
            if (dados.containsKey("meioPagamento")) m.setMeioPagamento(texto(dados, "meioPagamento"));
            if (dados.containsKey("comprovante")) m.setComprovante(texto(dados, "comprovante"));
        }
    }

    private static String texto(Map<String, Object> dados, String chave) {
        Object v = dados.get(chave);
        return v == null ? null : Textos.normalizar(v.toString());
    }

    private static int inteiro(Map<String, Object> dados, String chave) {
        Object v = dados.get(chave);
        if (v instanceof Number n) return n.intValue();
        try {
            return Integer.parseInt(String.valueOf(v).trim());
        } catch (NumberFormatException e) {
            throw new ValidacaoException("O campo '" + chave + "' deve ser um número inteiro.");
        }
    }

    private static BigDecimal decimal(Map<String, Object> dados, String chave) {
        Object v = dados.get(chave);
        try {
            return v instanceof BigDecimal b ? b : new BigDecimal(String.valueOf(v).trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new ValidacaoException("O campo '" + chave + "' deve ser um número decimal.");
        }
    }

    private static <E extends Enum<E>> E enumerado(Map<String, Object> dados, String chave, Class<E> tipo) {
        Object v = dados.get(chave);
        if (v == null) return null;
        if (tipo.isInstance(v)) return tipo.cast(v);
        try {
            return Enum.valueOf(tipo, v.toString().trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidacaoException("Valor inválido para '" + chave + "': " + v + ".");
        }
    }
}
