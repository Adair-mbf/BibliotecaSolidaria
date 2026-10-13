package br.com.bibliotecasolidaria.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public final class Domain {
    private Domain() {
    }

    public enum TipoUsuario {
        DOADOR, USUARIO_SITE, FUNCIONARIO
    }

    public enum TipoDoacao {
        LIVRO_FISICO, EBOOK, DINHEIRO
    }

    public enum StatusDoacao {
        REGISTRADA, EM_AVALIACAO, ACEITA, RECUSADA
    }

    public enum CategoriaLivro {
        DIDATICO, INFANTIL, OUTRO
    }

    public enum EstadoConservacao {
        OTIMO, BOM, ACEITAVEL, RUIM
    }

    public enum StatusItem {
        DISPONIVEL, EMPRESTADO, RESERVADO, INDISPONIVEL
    }

    public enum TipoItemAcervo {
        EXEMPLAR_FISICO, EBOOK
    }

    public enum StatusEmprestimo {
        ATIVO, ATRASADO, DEVOLVIDO
    }

    public enum StatusMulta {
        PENDENTE, PAGA
    }

    public enum StatusReserva {
        ATIVA, ATENDIDA, CANCELADA
    }

    public enum TipoBonificacao {
        PREMIO, CERTIFICADO, SELO, PARCERIA
    }

    public enum TipoEvento {
        LIVRO_DISPONIVEL, VENCIMENTO_PROXIMO, DOACAO_ACEITA
    }

    public record Usuario(
            Long id,
            String nome,
            String email,
            String senhaHash,
            boolean ativo,
            TipoUsuario tipo,
            String documento,
            Boolean pessoaJuridica,
            String telefone,
            String endereco,
            String matricula,
            String cargo) {
    }

    public record Doacao(
            Long id,
            Long doadorId,
            LocalDate dataRegistro,
            StatusDoacao status,
            TipoDoacao tipo,
            String titulo,
            String autor,
            String isbn,
            CategoriaLivro categoria,
            EstadoConservacao estado,
            Integer quantidade,
            String arquivoUrl,
            String formato,
            BigDecimal valor,
            String meioPagamento,
            String comprovante) {
    }

    public record Livro(
            Long id,
            String titulo,
            String autor,
            String editora,
            String isbn,
            CategoriaLivro categoria,
            int anoPublicacao) {
    }

    public record ItemAcervo(
            Long id,
            Long livroId,
            TipoItemAcervo tipo,
            StatusItem status,
            String codigoTombo,
            EstadoConservacao estado,
            String localizacao,
            String arquivoUrl,
            String formato) {
    }

    public record Emprestimo(
            Long id,
            Long usuarioId,
            Long itemAcervoId,
            LocalDate dataEmprestimo,
            LocalDate dataPrevistaDevolucao,
            LocalDate dataDevolucao,
            int renovacoes,
            StatusEmprestimo status) {
    }

    public record Multa(
            Long id,
            Long emprestimoId,
            BigDecimal valor,
            LocalDate dataGeracao,
            LocalDate dataPagamento,
            StatusMulta status) {
    }

    public record Reserva(
            Long id,
            Long usuarioId,
            Long livroId,
            LocalDate dataReserva,
            StatusReserva status) {
    }

    public record Bonificacao(
            Long id,
            Long doadorId,
            TipoBonificacao tipo,
            String descricao,
            LocalDate dataConcessao) {
    }

    public record Notificacao(
            Long id,
            Long usuarioId,
            TipoEvento tipo,
            String mensagem,
            LocalDateTime dataEnvio,
            boolean lida) {
    }
}
