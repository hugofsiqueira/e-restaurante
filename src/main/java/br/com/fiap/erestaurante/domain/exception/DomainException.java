package br.com.fiap.erestaurante.domain.exception;

/**
 * Exceção base para todas as regras de negócio violadas no domínio.
 * O GlobalExceptionHandler captura as subclasses para mapear ao código HTTP correto.
 */
public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }
}
