package br.com.fiap.erestaurante.core.usecase.tipousuario;

import br.com.fiap.erestaurante.core.domain.TipoUsuario;
import br.com.fiap.erestaurante.core.exception.TipoUsuarioJaCadastradoException;
import br.com.fiap.erestaurante.core.exception.TipoUsuarioNaoEncontradoException;
import br.com.fiap.erestaurante.core.gateway.TipoUsuarioGateway;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AtualizarTipoUsuarioUseCaseImpl implements AtualizarTipoUsuarioUseCase {

    private final TipoUsuarioGateway gateway;


    @Override
    public TipoUsuario execute(Long id, Command command) {
        TipoUsuario tipoUsuario = gateway.buscarPorId(id)
                .orElseThrow(() -> new TipoUsuarioNaoEncontradoException(id));

        if (!tipoUsuario.getNome().equalsIgnoreCase(command.nome())
                && gateway.existePorNome(command.nome())) {
            throw new TipoUsuarioJaCadastradoException(command.nome());
        }

        tipoUsuario.setNome(command.nome());
        return gateway.salvar(tipoUsuario);
    }
}
