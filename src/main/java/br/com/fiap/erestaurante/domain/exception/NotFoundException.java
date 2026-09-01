package br.com.fiap.erestaurante.domain.exception;

/**
 * Exceção base para recursos não encontrados. → HTTP 404
 */
public abstract class NotFoundException extends DomainException {

    protected NotFoundException(String message) {
        super(message);
    }
}
