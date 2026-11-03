package br.com.fiap.erestaurante.core.exception;

/**
 * Exceção base para todas as regras de negócio violadas no domínio.
 */
public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }
}
