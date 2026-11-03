package br.com.fiap.erestaurante.core.usecase.usuario;

import br.com.fiap.erestaurante.core.exception.UsuarioNaoEncontradoException;
import br.com.fiap.erestaurante.core.gateway.UsuarioGateway;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExcluirUsuarioUseCaseImpl implements ExcluirUsuarioUseCase {

    private final UsuarioGateway gateway;


    @Override
    public void execute(Long id) {
        if (gateway.buscarPorId(id).isEmpty()) {
            throw new UsuarioNaoEncontradoException(id);
        }
        gateway.excluir(id);
    }
}
