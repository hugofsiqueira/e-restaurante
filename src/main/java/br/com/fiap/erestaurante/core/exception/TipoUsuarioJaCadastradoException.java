package br.com.fiap.erestaurante.core.exception;

public class TipoUsuarioJaCadastradoException extends ConflictException {

    public TipoUsuarioJaCadastradoException(String nome) {
        super("Tipo de usuário já cadastrado: " + nome);
    }
}
