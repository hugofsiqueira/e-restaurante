package br.com.fiap.erestaurante.core.usecase.tipousuario;

import br.com.fiap.erestaurante.core.domain.TipoUsuario;
import br.com.fiap.erestaurante.core.exception.TipoUsuarioJaCadastradoException;
import br.com.fiap.erestaurante.core.gateway.TipoUsuarioGateway;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CriarTipoUsuarioUseCaseImpl implements CriarTipoUsuarioUseCase {

    private final TipoUsuarioGateway gateway;


    @Override
    public TipoUsuario execute(Command command) {
        if (gateway.existePorNome(command.nome())) {
            throw new TipoUsuarioJaCadastradoException(command.nome());
        }
        return gateway.salvar(TipoUsuario.builder().nome(command.nome()).build());
    }
}
