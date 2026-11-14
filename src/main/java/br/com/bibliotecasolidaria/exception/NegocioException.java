package br.com.bibliotecasolidaria.exception;

/** Violação de regra de negócio (HTTP 422). */
public class NegocioException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NegocioException(String mensagem) {
        super(mensagem);
    }

    public NegocioException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
