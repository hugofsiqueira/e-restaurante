package br.com.fiap.erestaurante.domain.exception;

public class EmailJaCadastradoException extends ConflictException {

    public EmailJaCadastradoException(String email) {
        super("Email já cadastrado: " + email);
    }
}
