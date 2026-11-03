package br.com.fiap.erestaurante.core.usecase.tipousuario;

import br.com.fiap.erestaurante.core.domain.TipoUsuario;
import br.com.fiap.erestaurante.core.exception.TipoUsuarioNaoEncontradoException;
import br.com.fiap.erestaurante.core.gateway.TipoUsuarioGateway;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class BuscarTipoUsuarioUseCaseImpl implements BuscarTipoUsuarioUseCase {

    private final TipoUsuarioGateway gateway;


    @Override
    public TipoUsuario buscarPorId(Long id) {
        return gateway.buscarPorId(id)
                .orElseThrow(() -> new TipoUsuarioNaoEncontradoException(id));
    }

    @Override
    public List<TipoUsuario> buscarTodos() {
        return gateway.buscarTodos();
    }
}
