package br.com.fiap.erestaurante.core.exception;

public class RestauranteNaoEncontradoException extends NotFoundException {

    public RestauranteNaoEncontradoException(Long id) {
        super("Restaurante não encontrado: " + id);
    }
}
