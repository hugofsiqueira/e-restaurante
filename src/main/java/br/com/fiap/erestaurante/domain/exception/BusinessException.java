package br.com.fiap.erestaurante.domain.exception;

/**
 * Exceção base para regras de negócio violadas (requisição inválida semanticamente). → HTTP 400
 */
public abstract class BusinessException extends DomainException {

    protected BusinessException(String message) {
        super(message);
    }
}
