package br.com.fiap.erestaurante.application.usecase;

import br.com.fiap.erestaurante.domain.exception.SenhaInvalidaException;
import br.com.fiap.erestaurante.domain.exception.UsuarioNaoEncontradoException;
import br.com.fiap.erestaurante.domain.port.in.TrocarSenhaUseCase;
import br.com.fiap.erestaurante.domain.port.out.UsuarioRepositoryPort;
import org.springframework.security.crypto.password.PasswordEncoder;

public class TrocarSenhaUseCaseImpl implements TrocarSenhaUseCase {

    private final UsuarioRepositoryPort repository;
    private final PasswordEncoder passwordEncoder;

    public TrocarSenhaUseCaseImpl(UsuarioRepositoryPort repository,
                                   PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void execute(Long id, Command command) {
        var usuario = repository.buscarPorId(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id));

        if (!passwordEncoder.matches(command.senhaAtual(), usuario.getSenha())) {
            throw new SenhaInvalidaException();
        }

        usuario.setSenha(passwordEncoder.encode(command.novaSenha()));
        repository.salvar(usuario);
    }
}
