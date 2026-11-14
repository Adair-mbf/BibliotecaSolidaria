package br.com.bibliotecasolidaria.exception;

/** Dado de entrada inválido ou ausente (HTTP 400). */
public class ValidacaoException extends NegocioException {
    private static final long serialVersionUID = 1L;

    public ValidacaoException(String mensagem) {
        super(mensagem);
    }

    public ValidacaoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
