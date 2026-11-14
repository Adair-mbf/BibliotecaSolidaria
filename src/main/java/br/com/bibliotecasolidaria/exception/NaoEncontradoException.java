package br.com.bibliotecasolidaria.exception;

/** Registro inexistente (HTTP 404). */
public class NaoEncontradoException extends NegocioException {
    private static final long serialVersionUID = 1L;

    public NaoEncontradoException(String mensagem) {
        super(mensagem);
    }

    public NaoEncontradoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
