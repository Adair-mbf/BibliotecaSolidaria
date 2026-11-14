package br.com.bibliotecasolidaria.exception;

/** Falha técnica de acesso ao banco de dados (HTTP 500). */
public class ErroPersistenciaException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ErroPersistenciaException(String mensagem) {
        super(mensagem);
    }

    public ErroPersistenciaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
