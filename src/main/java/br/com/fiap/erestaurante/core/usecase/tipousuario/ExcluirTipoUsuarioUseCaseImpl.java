package br.com.fiap.erestaurante.core.usecase.tipousuario;

import br.com.fiap.erestaurante.core.exception.TipoUsuarioNaoEncontradoException;
import br.com.fiap.erestaurante.core.gateway.TipoUsuarioGateway;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExcluirTipoUsuarioUseCaseImpl implements ExcluirTipoUsuarioUseCase {

    private final TipoUsuarioGateway gateway;


    @Override
    public void execute(Long id) {
        if (gateway.buscarPorId(id).isEmpty()) {
            throw new TipoUsuarioNaoEncontradoException(id);
        }
        gateway.excluir(id);
    }
}
