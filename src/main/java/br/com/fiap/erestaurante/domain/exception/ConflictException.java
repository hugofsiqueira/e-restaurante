package br.com.fiap.erestaurante.domain.exception;

/**
 * Exceção base para conflitos de estado (recurso já existente). → HTTP 409
 */
public abstract class ConflictException extends DomainException {

    protected ConflictException(String message) {
        super(message);
    }
}
