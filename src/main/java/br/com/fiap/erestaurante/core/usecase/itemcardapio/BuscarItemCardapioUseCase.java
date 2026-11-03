package br.com.fiap.erestaurante.core.usecase.itemcardapio;

import br.com.fiap.erestaurante.core.domain.ItemCardapio;

import java.util.List;

public interface BuscarItemCardapioUseCase {

    ItemCardapio buscarPorId(Long id);

    List<ItemCardapio> buscarPorRestaurante(Long restauranteId);
}
