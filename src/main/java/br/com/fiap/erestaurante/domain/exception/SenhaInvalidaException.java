package br.com.fiap.erestaurante.domain.exception;

public class SenhaInvalidaException extends BusinessException {

    public SenhaInvalidaException() {
        super("Senha atual incorreta.");
    }
}
