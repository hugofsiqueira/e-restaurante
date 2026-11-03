package br.com.fiap.erestaurante.core.usecase.itemcardapio;

import br.com.fiap.erestaurante.core.domain.ItemCardapio;
import br.com.fiap.erestaurante.core.exception.ItemCardapioNaoEncontradoException;
import br.com.fiap.erestaurante.core.gateway.ItemCardapioGateway;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AtualizarItemCardapioUseCaseImpl implements AtualizarItemCardapioUseCase {

    private final ItemCardapioGateway gateway;


    @Override
    public ItemCardapio execute(Long id, Command command) {
        var item = gateway.buscarPorId(id)
                .orElseThrow(() -> new ItemCardapioNaoEncontradoException(id));

        item.setNome(command.nome());
        item.setDescricao(command.descricao());
        item.setPreco(command.preco());
        item.setDisponivel(command.disponivel());
        item.setFotoPath(command.fotoPath());

        return gateway.salvar(item);
    }
}
