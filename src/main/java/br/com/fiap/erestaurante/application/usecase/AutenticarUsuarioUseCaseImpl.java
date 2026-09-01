package br.com.fiap.erestaurante.application.usecase;

import br.com.fiap.erestaurante.domain.exception.SenhaInvalidaException;
import br.com.fiap.erestaurante.domain.exception.UsuarioNaoEncontradoException;
import br.com.fiap.erestaurante.domain.model.Usuario;
import br.com.fiap.erestaurante.domain.port.in.AutenticarUsuarioUseCase;
import br.com.fiap.erestaurante.domain.port.out.UsuarioRepositoryPort;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AutenticarUsuarioUseCaseImpl implements AutenticarUsuarioUseCase {

    private final UsuarioRepositoryPort repository;
    private final PasswordEncoder passwordEncoder;

    public AutenticarUsuarioUseCaseImpl(UsuarioRepositoryPort repository,
                                        PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Usuario execute(Command command) {
        // Busca pelo login; retorna 404 genérico para não revelar se o login existe
        var usuario = repository.buscarPorLogin(command.login())
                .orElseThrow(() -> new UsuarioNaoEncontradoException(-1L));

        if (!passwordEncoder.matches(command.senha(), usuario.getSenha())) {
            throw new SenhaInvalidaException();
        }

        return usuario;
    }
}
