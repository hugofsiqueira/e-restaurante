package br.com.fiap.erestaurante.core.usecase.restaurante;

import br.com.fiap.erestaurante.core.domain.Restaurante;
import br.com.fiap.erestaurante.core.exception.RestauranteNaoEncontradoException;
import br.com.fiap.erestaurante.core.gateway.RestauranteGateway;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class BuscarRestauranteUseCaseImpl implements BuscarRestauranteUseCase {

    private final RestauranteGateway gateway;


    @Override
    public Restaurante buscarPorId(Long id) {
        return gateway.buscarPorId(id)
                .orElseThrow(() -> new RestauranteNaoEncontradoException(id));
    }

    @Override
    public List<Restaurante> buscarPorNome(String nome) {
        return gateway.buscarPorNome(nome);
    }
}
