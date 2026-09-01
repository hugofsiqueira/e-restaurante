package br.com.fiap.erestaurante.infrastructure.web.exception;

import br.com.fiap.erestaurante.domain.exception.BusinessException;
import br.com.fiap.erestaurante.domain.exception.ConflictException;
import br.com.fiap.erestaurante.domain.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.List;

/**
 * Captura as exceções de domínio e as converte em ProblemDetail (RFC 7807).
 *
 * Por capturar as classes BASE (NotFoundException, ConflictException, BusinessException),
 * qualquer nova exceção de domínio que herde dessas bases já recebe o status HTTP correto
 * automaticamente — sem precisar adicionar um novo @ExceptionHandler (OCP).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String BASE_TYPE = "https://e-restaurante.com/errors/";

    @ExceptionHandler(NotFoundException.class)
    ProblemDetail handleNotFound(NotFoundException ex) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Recurso não encontrado");
        problem.setType(URI.create(BASE_TYPE + "not-found"));
        return problem;
    }

    @ExceptionHandler(ConflictException.class)
    ProblemDetail handleConflict(ConflictException ex) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Conflito de dados");
        problem.setType(URI.create(BASE_TYPE + "conflict"));
        return problem;
    }

    @ExceptionHandler(BusinessException.class)
    ProblemDetail handleBusiness(BusinessException ex) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Regra de negócio violada");
        problem.setType(URI.create(BASE_TYPE + "business-error"));
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        var problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNPROCESSABLE_ENTITY, "Um ou mais campos são inválidos");
        problem.setTitle("Dados inválidos");
        problem.setType(URI.create(BASE_TYPE + "validation-error"));
        problem.setProperty("errors", errors);
        return problem;
    }
}
