package br.com.fiap.erestaurante.core.exception;

/**
 * Exceção base para recursos não encontrados. → HTTP 404
 */
public abstract class NotFoundException extends DomainException {

    protected NotFoundException(String message) {
        super(message);
    }
}
