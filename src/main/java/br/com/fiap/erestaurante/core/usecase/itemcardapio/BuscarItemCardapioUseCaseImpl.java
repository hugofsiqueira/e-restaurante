package br.com.fiap.erestaurante.core.usecase.itemcardapio;

import br.com.fiap.erestaurante.core.domain.ItemCardapio;
import br.com.fiap.erestaurante.core.exception.ItemCardapioNaoEncontradoException;
import br.com.fiap.erestaurante.core.gateway.ItemCardapioGateway;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class BuscarItemCardapioUseCaseImpl implements BuscarItemCardapioUseCase {

    private final ItemCardapioGateway gateway;


    @Override
    public ItemCardapio buscarPorId(Long id) {
        return gateway.buscarPorId(id)
                .orElseThrow(() -> new ItemCardapioNaoEncontradoException(id));
    }

    @Override
    public List<ItemCardapio> buscarPorRestaurante(Long restauranteId) {
        return gateway.buscarPorRestaurante(restauranteId);
    }
}
