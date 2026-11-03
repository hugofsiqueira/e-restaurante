package br.com.fiap.erestaurante.core.exception;

public class TipoUsuarioNaoEncontradoException extends NotFoundException {

    public TipoUsuarioNaoEncontradoException(Long id) {
        super("Tipo de usuário não encontrado com id: " + id);
    }
}
