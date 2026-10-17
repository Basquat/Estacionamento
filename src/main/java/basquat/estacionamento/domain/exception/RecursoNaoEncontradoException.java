package basquat.estacionamento.domain.exception;

/**
 * Exceção de domínio - Recurso não encontrado.
 */
public class RecursoNaoEncontradoException extends RuntimeException {
    public RecursoNaoEncontradoException(String message) {
        super(message);
    }
}

