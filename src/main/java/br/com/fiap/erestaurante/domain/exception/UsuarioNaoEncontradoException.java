package br.com.fiap.erestaurante.domain.exception;

public class UsuarioNaoEncontradoException extends NotFoundException {

    public UsuarioNaoEncontradoException(Long id) {
        super("Usuário não encontrado com id: " + id);
    }
}
