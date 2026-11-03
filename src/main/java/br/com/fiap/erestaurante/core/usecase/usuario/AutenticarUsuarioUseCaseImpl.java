package br.com.fiap.erestaurante.core.usecase.usuario;

import br.com.fiap.erestaurante.core.domain.Usuario;
import br.com.fiap.erestaurante.core.exception.SenhaInvalidaException;
import br.com.fiap.erestaurante.core.exception.UsuarioNaoEncontradoException;
import br.com.fiap.erestaurante.core.gateway.UsuarioGateway;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AutenticarUsuarioUseCaseImpl implements AutenticarUsuarioUseCase {

    private final UsuarioGateway gateway;
    private final PasswordEncoder passwordEncoder;


    @Override
    public Usuario execute(Command command) {
        var usuario = gateway.buscarPorLogin(command.login())
                .orElseThrow(() -> new UsuarioNaoEncontradoException(-1L));

        if (!passwordEncoder.matches(command.senha(), usuario.getSenha())) {
            throw new SenhaInvalidaException();
        }

        return usuario;
    }
}
