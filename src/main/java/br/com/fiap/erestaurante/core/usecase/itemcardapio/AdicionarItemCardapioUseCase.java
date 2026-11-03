package br.com.fiap.erestaurante.core.usecase.itemcardapio;

import br.com.fiap.erestaurante.core.domain.ItemCardapio;

import java.math.BigDecimal;

public interface AdicionarItemCardapioUseCase {

    record Command(
            String nome,
            String descricao,
            BigDecimal preco,
            boolean disponivel,
            String fotoPath,
            Long restauranteId
    ) {}

    ItemCardapio execute(Command command);
}
