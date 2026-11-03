package br.com.fiap.erestaurante.core.exception;

public class UsuarioNaoEncontradoException extends NotFoundException {

    public UsuarioNaoEncontradoException(Long id) {
        super("Usuário não encontrado com id: " + id);
    }
}
