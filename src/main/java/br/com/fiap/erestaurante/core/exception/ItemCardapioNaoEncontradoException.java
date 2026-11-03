package br.com.fiap.erestaurante.core.exception;

public class ItemCardapioNaoEncontradoException extends NotFoundException {

    public ItemCardapioNaoEncontradoException(Long id) {
        super("Item de cardápio não encontrado: " + id);
    }
}
