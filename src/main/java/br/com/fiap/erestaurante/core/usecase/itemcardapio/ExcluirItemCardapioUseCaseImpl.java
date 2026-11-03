package br.com.fiap.erestaurante.core.usecase.itemcardapio;

import br.com.fiap.erestaurante.core.exception.ItemCardapioNaoEncontradoException;
import br.com.fiap.erestaurante.core.gateway.ItemCardapioGateway;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExcluirItemCardapioUseCaseImpl implements ExcluirItemCardapioUseCase {

    private final ItemCardapioGateway gateway;


    @Override
    public void execute(Long id) {
        if (gateway.buscarPorId(id).isEmpty()) {
            throw new ItemCardapioNaoEncontradoException(id);
        }
        gateway.excluir(id);
    }
}
