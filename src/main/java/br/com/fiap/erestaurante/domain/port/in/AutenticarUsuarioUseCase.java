package br.com.fiap.erestaurante.domain.port.in;

import br.com.fiap.erestaurante.domain.model.Usuario;

/**
 * Porta de entrada — autenticação de usuário (login).
 * Sem Spring Security: validação simples de login + senha com BCrypt.
 */
public interface AutenticarUsuarioUseCase {

    record Command(
            String login,
            String senha
    ) {}

    Usuario execute(Command command);
}
