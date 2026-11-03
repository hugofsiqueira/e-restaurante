package br.com.fiap.erestaurante.core.exception;

public class DonoRestauranteInvalidoException extends BusinessException {

    public DonoRestauranteInvalidoException(Long id) {
        super("Usuário " + id + " não é um Dono de Restaurante");
    }
}
