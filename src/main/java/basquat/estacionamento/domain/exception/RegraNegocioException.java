package basquat.estacionamento.domain.exception;

/**
 * Exceção de domínio - Regra de negócio violada.
 */
public class RegraNegocioException extends RuntimeException {
    public RegraNegocioException(String message) {
        super(message);
    }
}