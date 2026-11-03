package br.com.fiap.erestaurante.core.usecase.usuario;

import br.com.fiap.erestaurante.core.exception.SenhaInvalidaException;
import br.com.fiap.erestaurante.core.exception.UsuarioNaoEncontradoException;
import br.com.fiap.erestaurante.core.gateway.UsuarioGateway;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TrocarSenhaUseCaseImpl implements TrocarSenhaUseCase {

    private final UsuarioGateway gateway;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void execute(Long id, Command command) {
        var usuario = gateway.buscarPorId(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id));

        if (!passwordEncoder.matches(command.senhaAtual(), usuario.getSenha())) {
            throw new SenhaInvalidaException();
        }

        usuario.setSenha(passwordEncoder.encode(command.novaSenha()));
        gateway.salvar(usuario);
    }
}
