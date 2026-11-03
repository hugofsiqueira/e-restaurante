package br.com.fiap.erestaurante.core.usecase.itemcardapio;

import br.com.fiap.erestaurante.core.domain.ItemCardapio;

import java.math.BigDecimal;

public interface AtualizarItemCardapioUseCase {

    record Command(
            String nome,
            String descricao,
            BigDecimal preco,
            boolean disponivel,
            String fotoPath
    ) {}

    ItemCardapio execute(Long id, Command command);
}
