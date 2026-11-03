package br.com.fiap.erestaurante.core.usecase.restaurante;

import br.com.fiap.erestaurante.core.exception.RestauranteNaoEncontradoException;
import br.com.fiap.erestaurante.core.gateway.RestauranteGateway;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExcluirRestauranteUseCaseImpl implements ExcluirRestauranteUseCase {

    private final RestauranteGateway gateway;


    @Override
    public void execute(Long id) {
        gateway.buscarPorId(id)
                .orElseThrow(() -> new RestauranteNaoEncontradoException(id));

        gateway.excluir(id);
    }
}
