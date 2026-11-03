package br.com.fiap.erestaurante.core.usecase.itemcardapio;

import br.com.fiap.erestaurante.core.domain.ItemCardapio;
import br.com.fiap.erestaurante.core.exception.RestauranteNaoEncontradoException;
import br.com.fiap.erestaurante.core.gateway.ItemCardapioGateway;
import br.com.fiap.erestaurante.core.gateway.RestauranteGateway;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AdicionarItemCardapioUseCaseImpl implements AdicionarItemCardapioUseCase {

    private final ItemCardapioGateway gateway;
    private final RestauranteGateway restauranteGateway;


    @Override
    public ItemCardapio execute(Command command) {
        var restaurante = restauranteGateway.buscarPorId(command.restauranteId())
                .orElseThrow(() -> new RestauranteNaoEncontradoException(command.restauranteId()));

        var item = ItemCardapio.builder()
                .nome(command.nome())
                .descricao(command.descricao())
                .preco(command.preco())
                .disponivel(command.disponivel())
                .fotoPath(command.fotoPath())
                .restaurante(restaurante)
                .build();

        return gateway.salvar(item);
    }
}
