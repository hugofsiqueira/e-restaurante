package br.com.fiap.erestaurante.core.exception;

/**
 * Exceção base para regras de negócio violadas semanticamente. → HTTP 400
 */
public abstract class BusinessException extends DomainException {

    protected BusinessException(String message) {
        super(message);
    }
}
