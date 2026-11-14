package br.com.bibliotecasolidaria.exception;

/** Conflito com o estado atual dos dados, como e-mail duplicado ou registro referenciado (HTTP 409). */
public class ConflitoException extends NegocioException {
    private static final long serialVersionUID = 1L;

    public ConflitoException(String mensagem) {
        super(mensagem);
    }

    public ConflitoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
