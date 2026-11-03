package br.com.fiap.erestaurante.core.usecase.usuario;

import br.com.fiap.erestaurante.core.domain.Usuario;

public interface AutenticarUsuarioUseCase {

    record Command(
            String login,
            String senha
    ) {}

    Usuario execute(Command command);
}
