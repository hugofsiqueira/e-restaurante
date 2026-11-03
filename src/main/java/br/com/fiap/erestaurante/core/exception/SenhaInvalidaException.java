package br.com.fiap.erestaurante.core.exception;

public class SenhaInvalidaException extends BusinessException {

    public SenhaInvalidaException() {
        super("Senha atual incorreta.");
    }
}
